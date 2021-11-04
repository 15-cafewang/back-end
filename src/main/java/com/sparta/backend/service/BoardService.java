package com.sparta.backend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardImage;
import com.sparta.backend.domain.BoardLikes;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import com.sparta.backend.dto.request.board.PutBoardRequestDto;
import com.sparta.backend.dto.response.board.GetBoardDetailResponseDto;
import com.sparta.backend.dto.response.board.GetBoardResponseDto;
import com.sparta.backend.repository.BoardImageRepository;
import com.sparta.backend.repository.BoardLikesRepository;
import com.sparta.backend.repository.BoardRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final BoardLikesRepository boardLikesRepository;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";
    private final int maxImageCount = 5;    //사진 업로드 최대 개수

    //게시물 등록
    @Transactional
    public Long createBoard(PostBoardRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        User user = userDetails.getUser();
        MultipartFile[] image = requestDto.getImage();
        List<String> imageList = new ArrayList<>();

        for(MultipartFile img : image) {
            String imageUrl = s3Uploader.upload(img, "boardImage");
            System.out.println("saveImage: " + imageUrl);
            if(imageUrl == null)    //이미지 업로드에 실패했을 때
                throw new NullPointerException("이미지 업로드에 실패하였습니다.");
            imageList.add(imageUrl);
        }

        Long boardId = 0L;
        if(user != null) {
            Board board = new Board(requestDto, user);
            Board savedBoard = boardRepository.save(board);
            boardId = savedBoard.getId();

            for(String img : imageList) {
                try {
                    BoardImage boardImage = new BoardImage(img, board);
                    boardImageRepository.save(boardImage);
                } catch (Exception e) {
                    deleteS3(img);
                }
            }
        } else {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return boardId;
    }

    //전체 게시물 조회
    public Page<GetBoardResponseDto> getBoards(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        User currentLoginUser = userDetails.getUser();

        //정렬 기준
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        //어떤 컬럼 기준으로 정렬할 지 결정(sortBy: 컬럼이름)
        Sort sort = Sort.by(direction, sortBy);
        //페이징
        Pageable pageable = PageRequest.of(page, size, sort);

       Page<Board> boardList = boardRepository.findAll(pageable);

       //Page<Board> -> Page<Dto> 로 변환
       Page<GetBoardResponseDto> responseDtoList = boardList.map(board -> new GetBoardResponseDto(
                board.getId(), board.getUser().getNickname(), board.getTitle(), board.getContent(),
                board.getBoardImageList().get(0).getImage(), board.getRegDate(), board.getBoardCommentList().size(),
                board.getBoardLikesList().size(),
               (boardLikesRepository.findByBoardAndUser(board, currentLoginUser) != null)
       ));

        return responseDtoList;
    }

    //게시물 상세 조회
    public GetBoardDetailResponseDto getBoardDetail(Long id, UserDetailsImpl userDetails) {
        User currentLoginUser = userDetails.getUser();

        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NullPointerException("찾는 게시물이 없습니다.")
        );

        GetBoardDetailResponseDto responseDto = null;
        if(board != null) {
            Long boardId = board.getId();
            String title = board.getTitle();
            String nickname = board.getUser().getNickname();
            String profile = board.getUser().getImage();
            LocalDateTime regDate = board.getRegDate();
            String content = board.getContent();
            List<BoardImage> boardimageList = board.getBoardImageList();
            List<String> images = new ArrayList<>();
            for(BoardImage boardImage : boardimageList) {
                String image = boardImage.getImage();
                images.add(image);
            }
            int likeCount = board.getBoardLikesList().size();
            BoardLikes boardLikes = boardLikesRepository.findByBoardAndUser(board, currentLoginUser);
            boolean likeStatus = boardLikes != null;

            responseDto = new GetBoardDetailResponseDto(boardId, title, nickname, profile, regDate,
                                                        content, images, likeCount, likeStatus);
        }

        return responseDto;
    }

    //게시물 수정
    @Transactional
    public Board updateBoard(Long id, PutBoardRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        Board board = null;
        String title = requestDto.getTitle();
        String content = requestDto.getContent();

        if(userDetails != null) {   //로그인 했을 때
            String currentLoginEmail = userDetails.getUser().getEmail();
            board = boardRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("찾는 게시물이 없습니다.")
            );

            String writterEmail = board.getUser().getEmail();   //게시물을 작성한 계정 아이디: 이메일

            if(currentLoginEmail.equals(writterEmail)) {    //작성자가 현재 로그인한 사용자일 때
                String[] imageUrlList =  requestDto.getImageUrl();  //ex: [https://...aaa.png, "", "" , https://...aab.png, https://...aac.png]
                MultipartFile[] imageList = requestDto.getImage();  //ex: filename: ["", "abc.png", "def.png", "", ""]

                //수정할 이미지를 S3에 올림
                for(int i=0; i<imageList.length; i++) {
                    MultipartFile image = imageList[i];
                    if(!image.getOriginalFilename().equals("") || image.getSize() > 0) {    //업로드한 이미지가 있을 때
                        String uploadImageUrl = s3Uploader.upload(image, "boardImage");
                        if(uploadImageUrl == null)    //이미지 업로드에 실패했을 때
                            throw new NullPointerException("이미지 업로드에 실패하였습니다.");
                        imageUrlList[i] = uploadImageUrl;
                    }
                }

                //imageUrlList에는 수정한 결과 이미지 URL이 담겨있는 상태
                //imageUrlList에 있는 모든 Url을 반영해야 함
                List<BoardImage> boardImageList = boardImageRepository.findAllByBoard(board);

                //outOfIndex를 피하기 위해 저장 공간의 개수를 맞춰줌
                String[] exisitImageList = new String[maxImageCount];

                for(int i=0; i<boardImageList.size(); i++) {
                    BoardImage boardImage = boardImageList.get(i);  //게시물 작성 시 첨부했던 이미지
                    String image = boardImage.getImage();
                    exisitImageList[i] = image;
                }

                for(int i=0; i<exisitImageList.length; i++) {
                    if(!imageUrlList[i].equals(exisitImageList[i])) {

                        //사용자가 게시물 수정 시 기존에 있던 이미지를 삭제한 경우
                        if(imageUrlList[i].equals("") && exisitImageList[i] != null) {
                            deleteS3(exisitImageList[i]);
                            Long boardImageId = boardImageList.get(i).getId();
                            boardImageRepository.deleteById(boardImageId);
                        }
                        //사용자가 새 이미지를 추가한 경우
                        else if(imageUrlList[i].length() > 0 && exisitImageList[i] == null) {
                            BoardImage newBoardImage = new BoardImage(imageUrlList[i], board);
                            boardImageRepository.save(newBoardImage);
                        }
                        //게시물 작성 시 올린 이미지도, 새롭게 올린 이미지도 없을 경우
                        else if(imageUrlList[i].equals("") && exisitImageList[i] == null) {
                            break;
                        }
                        //사용자가 게시물 수정 시 기존에 있던 이미지를 수정한 경우
                        else {
                            BoardImage boardImage = boardImageList.get(i);
                            deleteS3(exisitImageList[i]);    //기존에 올린 이미지는 삭제
                            boardImage.updateImage(imageUrlList[i]);  //새로 올린 이미지로 update
                        }
                    }
                }
            } else {    //작성자와 현재 로그인한 사용자가 다를 때
                throw new IllegalArgumentException("게시물을 작성한 사용자만 수정 가능합니다.");
            }
        } else {    //로그인 안 했을 때
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return board.updateBoard(title, content);
    }

    //게시물 삭제
    @Transactional
    public Long deleteBoard(Long id, UserDetailsImpl userDetails) {
        if(userDetails != null) {   //로그인 했을 경우
            String currentLoginEmail = userDetails.getUser().getEmail();
            Board board = boardRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("찾는 게시물이 없습니다.")
            );

            String writerEmail = board.getUser().getEmail();

            if(writerEmail.equals(currentLoginEmail)) { //작성자와 현재 로그인한 사용자 계정이 동일할 때
                List<BoardImage> boardImageList = boardImageRepository.findAllByBoard(board);
                for(BoardImage bi : boardImageList) {
                    String image = bi.getImage();
                    deleteS3(image);
                    boardImageRepository.deleteAllByBoard(board);
                }
                boardRepository.deleteById(id);
            } else { //현재 로그인한 사용자 계정이 작성자가 아닐 때
                throw new IllegalArgumentException("게시물을 작성한 사용자만 삭제 가능합니다.");
            }
        } else {    //로그인 안 했을 경우
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return id;
    }

    //S3 이미지 삭제
    public void deleteS3(String imageName){
        //https://S3 버킷 URL/버킷에 생성한 폴더명/이미지이름
        String keyName = imageName.split("/")[4]; // 이미지이름만 추출

        try {
            amazonS3Client.deleteObject(bucket + "/boardImage", keyName);
            System.out.println("S3에서 삭제한 파일 이름: " + keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }
}
