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

    //게시물 등록
    @Transactional
    public Long createBoard(PostBoardRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        User user = userDetails.getUser();
        MultipartFile[] image = requestDto.getImage();
        List<String> imageList = new ArrayList<>();

        Long boardId = 0L;
        if(user != null) {
            Board board = new Board(requestDto, user);
            Board savedBoard = boardRepository.save(board);
            boardId = savedBoard.getId();

            if(image != null) { //업로드한 이미지가 있을 경우
                //S3에 이미지 업로드
                for(MultipartFile img : image) {
                    String imageUrl = s3Uploader.upload(img, "boardImage");
                    System.out.println("saveImage: " + imageUrl);
                    if(imageUrl == null)    //이미지 업로드에 실패했을 때
                        throw new NullPointerException("이미지 업로드에 실패하였습니다.");
                    imageList.add(imageUrl);
                }

                //이미지 URL을 DB에 저장
                for (String img : imageList) {
                    try {
                        BoardImage boardImage = new BoardImage(img, board);
                        boardImageRepository.save(boardImage);
                    } catch (Exception e) {
                        deleteS3(img);
                    }
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
        //현재 페이지
        page = page - 1;

        //정렬 기준
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        //어떤 컬럼 기준으로 정렬할 지 결정(sortBy: 컬럼이름)
        Sort sort = Sort.by(direction, sortBy);
        //페이징
        Pageable pageable = PageRequest.of(page, size, sort);

       Page<Board> boardList = boardRepository.findAll(pageable);

       //Page<Board> -> Page<Dto> 로 변환
       Page<GetBoardResponseDto> responseDtoList = boardList.map(board ->
               new GetBoardResponseDto(board, currentLoginUser, boardLikesRepository));

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
            List<BoardImage> boardImageList = board.getBoardImageList();
            List<String> images = new ArrayList<>();
            if(board.getBoardImageList().size() > 0) {  //이미지가 있을 경우
                for(BoardImage boardImage : boardImageList) {
                    String image = boardImage.getImage();
                    images.add(image);
                }
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
                MultipartFile[] imageList = requestDto.getImage();

                if(imageList != null) {   //업로드할 이미지가 존재할 떄
                    List<BoardImage> boardImageList = boardImageRepository.findAllByBoard(board);

                    //기존 이미지들을 S3에서 삭제
                    for(BoardImage boardImage : boardImageList) {
                        String deleteUrl = boardImage.getImage();
                        deleteS3(deleteUrl);
                    }
                    //기존 이미지들을 DB에서 삭제
                    boardImageRepository.deleteAllByBoard(board);

                    int imageListLength = imageList.length;
                    String[] uploadImageList = new String[imageListLength]; //URL로 변환한 이미지들을 담을 배열

                    //수정할 이미지를 S3에 올림
                    for(int i=0; i<imageListLength; i++) {
                        MultipartFile image = imageList[i];
                        if(!image.getOriginalFilename().equals("") || image.getSize() > 0) {    //업로드한 이미지가 있을 때
                            String uploadImageUrl = s3Uploader.upload(image, "boardImage");
                            if(uploadImageUrl == null)    //이미지 업로드에 실패했을 때
                                throw new NullPointerException("이미지 업로드에 실패하였습니다.");
                            uploadImageList[i] = uploadImageUrl;
                        }
                    }

                    //새로 첨부한 이미지들을 DB에 넣기
                    for(String url : uploadImageList) {
                        BoardImage boardImage = new BoardImage(url, board);
                        boardImageRepository.save(boardImage);
                    }
                } else {    //수정 시 업로드할 이미지를 첨부 안 하고 수정완료 버튼을 누를 시 기존 이미지들을 모두 삭제
                    //기존 이미지들을 DB에서 삭제
                    boardImageRepository.deleteAllByBoard(board);
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
                }
                boardImageRepository.deleteAllByBoard(board);
                boardRepository.deleteById(id);
            } else { //현재 로그인한 사용자 계정이 작성자가 아닐 때
                throw new IllegalArgumentException("게시물을 작성한 사용자만 삭제 가능합니다.");
            }
        } else {    //로그인 안 했을 경우
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return id;
    }

    //게시물 검색
    public Page<GetBoardResponseDto> searchBoards(String keword, int page, int size, boolean isAsc, String sortBy,
                                                  UserDetailsImpl userDetails) {
        //현재 페이지
        page = page - 1;

        User currentLoginUser = userDetails.getUser();

        //정렬 기준
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        //어떤 컬럼 기준으로 정렬할 지 결정(sortBy: 컬럼이름)
        Sort sort = Sort.by(direction, sortBy);
        //페이징
        Pageable pageable = PageRequest.of(page, size, sort);

        //키워드가 제목 또는 내용에 포함되어있어야 검색 결과에 나타남
        String title = keword;
        String content = keword;
        Page<Board> boardList = boardRepository.findAllByTitleContainingOrContentContaining(title, content, pageable);

        //Page<Board> -> Page<Dto> 로 변환
        Page<GetBoardResponseDto> responseDtoList = boardList.map(board ->
                new GetBoardResponseDto(board, currentLoginUser, boardLikesRepository));

        return responseDtoList;
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
