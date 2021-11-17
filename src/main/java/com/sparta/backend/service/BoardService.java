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
    private final int maxImageCount = 5;

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
        boolean isLikeCount = false;

        //정렬 기준
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;

        if(sortBy.equals("likeCount")) {
            sortBy = "regDate";
            isLikeCount = true;
        }

        //어떤 컬럼 기준으로 정렬할 지 결정(sortBy: 컬럼이름)
        Sort sort = Sort.by(direction, sortBy);

        //페이징
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardList = null;
        if(isLikeCount) {
            boardList = boardRepository.findBoardsOrderByLikeCountDesc(pageable);
        } else {
            boardList = boardRepository.findAll(pageable);
        }

        // Page<Board> -> Page<Dto> 로 변환
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

        loginCheck(userDetails);    //로그인했는지 확인

        board = boardRepository.findById(id).orElseThrow(
                () -> new NullPointerException("찾는 게시물이 없습니다.")
        );

        String currentLoginEmail = userDetails.getUser().getEmail();
        String writterEmail = board.getUser().getEmail();   //게시물을 작성한 계정 아이디: 이메일
        writterCheck(currentLoginEmail, writterEmail); //로그인한 계정이 게시물 작성자인지 확인

        /* 로그인한 상태 && 로그인 계정이 게시물 작성자일 때 아래 로직 수행 */

        MultipartFile[] imageList = requestDto.getImage();  //추가할 이미지 파일 리스트
        List<String> deleteImageList = requestDto.getDeleteImage(); //삭제할 이미지URL 리스트

        List<BoardImage> boardImage = boardImageRepository.findAllByBoard(board);

        //게시물 수정 후 게시물에 이미지가 몇 장 있을 지 확인
        //게시물 작성 시 첨부했던 이미지와 새로 올린 이미지, 삭제할 이미지를 모두 고려해서 5장이 넘어간 경우
        //exception 발생
        imageCountCheck(imageList, deleteImageList, boardImage);

        /* 새로 첨부한 이미지 파일 추가 */
        updateNewImageFiles(board, imageList);

        /* 삭제해야하는 이미지 삭제 */
        deleteOldImages(board, deleteImageList);

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
    public Page<GetBoardResponseDto> searchBoards(String keyword, int page, int size, boolean isAsc, String sortBy,
                                                  UserDetailsImpl userDetails) {
        //현재 페이지
        page = page - 1;
        boolean isLikeCount = false;

        User currentLoginUser = userDetails.getUser();

        if(sortBy.equals("likeCount")) {
            sortBy = "regDate";
            isLikeCount = true;
        }

        //정렬 기준
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        //어떤 컬럼 기준으로 정렬할 지 결정(sortBy: 컬럼이름)
        Sort sort = Sort.by(direction, sortBy);
        //페이징
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Board> boardList = null;
        keyword = keyword.trim();   //앞뒤 공백 제거
        if(isLikeCount) {
            //키워드가 제목 또는 내용에 포함되어있어야 검색 결과에 나타남
            boardList =
                    boardRepository.findBoardsByTitleContainingOrContentContainingOrderByLikeCountDesc(keyword, pageable);
        } else {
            //키워드가 제목 또는 내용에 포함되어있어야 검색 결과에 나타남
            boardList = boardRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, pageable);
        }

        //Page<Board> -> Page<Dto> 로 변환
        Page<GetBoardResponseDto> responseDtoList = boardList.map(board ->
                new GetBoardResponseDto(board, currentLoginUser, boardLikesRepository));

        return responseDtoList;
    }

    //게시물 수정 후 게시물에 이미지가 몇 장 있을 지 확인
    private void imageCountCheck(MultipartFile[] imageList, List<String> deleteImageList, List<BoardImage> boardImage) {
        if(boardImage != null && boardImage.size() > 0) {
            if(imageList != null && imageList.length > 0) {
                //imageList에 들어있는 실제 이미지 파일들의 개수
                int imageFileCount = getImageFileCount(imageList);
                int imageCount = boardImage.size() + imageFileCount;
                if(imageCount > maxImageCount)
                    throw new IllegalArgumentException("한 게시물에 이미지는 최대 5장 존재할 수 있습니다.");
            }
            if((imageList != null && imageList.length > 0) && (deleteImageList != null && deleteImageList.size() > 0)) {
                //imageList에 들어있는 실제 이미지 파일들의 개수
                int imageFileCount = getImageFileCount(imageList);
                int imageCount = boardImage.size() + imageFileCount - deleteImageList.size();
                if(imageCount > maxImageCount)
                    throw new IllegalArgumentException("한 게시물에 이미지는 최대 5장 존재할 수 있습니다.");
            }
        }
    }

    //imageList에 들어있는 실제 이미지 파일들의 개수 구하기
    private int getImageFileCount(MultipartFile[] imageList) {
        int imageFileCount = 0;
        for (MultipartFile image : imageList) {
            //이미지를 첨부 안 헀어도 리스트에 무언가가 들어옴
            //정확히 판단해내려면 리스트에 담겨있는 파일의 크기가 0이 넘는지 확인해야함
            if (image.getSize() > 0) {   //파일 크기가 0이 넘으면
                imageFileCount++;       //새로 올릴 파일이 존재하므로 count
            }
        }
        return imageFileCount;
    }

    //게시물 수정 시 새로 첨부한 이미지들은 DB와 S3에 넣기
    private void updateNewImageFiles(Board board, MultipartFile[] imageList) {
        if(imageList != null && imageList.length > 0) { //새로 첨부한 이미지가 존재할 때
            //S3에 업로드
            List<String> uploadImageUrlList = new ArrayList<>();    //S3에 업로드된 이미지URL을 담는 용도
            String uploadImageUrl = null;
            try {
                for(MultipartFile imageFile : imageList) {
                    uploadImageUrl = s3Uploader.upload(imageFile, "boardImage");
                    if(uploadImageUrl == null)    //이미지 업로드에 실패했을 때
                        throw new NullPointerException("이미지 업로드에 실패하였습니다.");
                    uploadImageUrlList.add(uploadImageUrl);
                }
            } catch(Exception exception) {
                //지금까지 업로드한 이미지들을 롤백: S3에서 삭제
                for(String url: uploadImageUrlList) {
                    deleteS3(url);
                }
            }

            //DB에 Insert
            for(String imageUrl : uploadImageUrlList) {
                BoardImage newBoardImage = new BoardImage(imageUrl, board);
                boardImageRepository.save(newBoardImage);
            }
        }
    }

    //게시물 수정 시 삭제해야하는 이미지들을 DB와 S3에서 삭제하기
    private void deleteOldImages(Board board, List<String> deleteImageList) {
        if(deleteImageList != null && deleteImageList.size() > 0) { //삭제할 이미지가 존재할 때

            List<BoardImage> deleteBoardImageList = boardImageRepository.findAllByImageIn(deleteImageList);
            List<String> deleteImageListInDB = new ArrayList<>();   //DB에서 조회해서 나온 이미지URL을 담는 용도
            for (BoardImage deleteBoardImage : deleteBoardImageList) {
                String deleteUrl = deleteBoardImage.getImage();
                Board findBoard = deleteBoardImage.getBoard();
                //현재 이미지URL이 해당 게시물의 이미지가 맞을 경우 삭제할 이미지URL 리스트에 담기
                if(board.getId().equals(findBoard.getId())) {
                    deleteImageListInDB.add(deleteUrl);
                }
            }
            //DB에서 조회된 이미지URL들만 DB에서 삭제
            boardImageRepository.deleteByImageIn(deleteImageListInDB);
            //DB에서 조회된 이미지URL들만 S3에서 삭제
            for(String url : deleteImageListInDB) {
                deleteS3(url);
            }
        }
    }

    //로그인 되어있는지 확인하기
    private void loginCheck(UserDetailsImpl userDetails) {
        if(userDetails == null) {   //로그인 안 했을 떄
            throw new NullPointerException("로그인이 필요합니다.");
        }
    }

    //로그인한 계정이 작성자가 맞는지 확인하기
    private void writterCheck(String currentLoginEmail, String writterEmail) {
        if (!currentLoginEmail.equals(writterEmail)) {  //로그인한 계정이 작성자가 아닐 때
            throw new IllegalArgumentException("게시물을 작성한 사용자만 수정 가능합니다.");
        }
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
