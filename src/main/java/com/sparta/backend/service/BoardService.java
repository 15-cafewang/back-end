package com.sparta.backend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import com.sparta.backend.dto.request.board.PutBoardRequestDto;
import com.sparta.backend.repository.BoardRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;


@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";

    //게시물 등록
    @Transactional
    public Long createBoard(PostBoardRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        User user = userDetails.getUser();
        MultipartFile image = requestDto.getImage();
        String saveImage = s3Uploader.upload(image, "boardImage");
        System.out.println("savaImage: " + saveImage);

        Long boardId = 0L;
        if(user != null) {
            Board board = new Board(requestDto, saveImage, user);
            Board savedBoard = boardRepository.save(board);
            boardId = savedBoard.getId();
        } else {
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return boardId;
    }

    //게시물 수정
    @Transactional
    public Board updateBoard(Long id, PutBoardRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        Board board = null;
        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        String imageUrl = "";

        if(userDetails != null) {   //로그인 했을 때
            String currentLoginEmail = userDetails.getUser().getEmail();
            board = boardRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("찾는 게시물이 없습니다.")
            );
            imageUrl = board.getImage();    //수정 전 이미지 URL
            String writterEmail = board.getUser().getEmail();

            if(currentLoginEmail.equals(writterEmail)) {    //작성자가 현재 로그인한 사용자일 때
                if(requestDto.getImage() != null) {     //수정할 이미지가 있을 떄
                    deleteS3(board.getImage());     //기존에 등록되어있던 이미지 삭제
                    MultipartFile image = requestDto.getImage();
                    imageUrl = s3Uploader.upload(image, "boardImage");  //새 이미지 등록. return 이미지 URL
                    System.out.println("수정한 이미지 URL: " + imageUrl);
                    if(imageUrl == null)    //이미지 업로드에 실패했을 때
                        throw new NullPointerException("이미지 업로드에 실패하였습니다.");
                }
            } else {    //작성자와 현재 로그인한 사용자가 다를 때
                throw new IllegalArgumentException("게시물을 작성한 사용자만 수정 가능합니다.");
            }
        } else {    //로그인 안 했을 때
            throw new NullPointerException("로그인이 필요합니다.");
        }

        return board.update(title, content, imageUrl);
    }

    //게시물 삭제
    @Transactional
    public Long deleteBoard(Long id, UserDetailsImpl userDetails) {
        String currentLoginEmail = userDetails.getUser().getEmail();
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NullPointerException("찾는 게시물이 없습니다.")
        );
        String writerEmail = board.getUser().getEmail();

        if(writerEmail.equals(currentLoginEmail)) {
            deleteS3(board.getImage());
            boardRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("게시물을 작성한 사용자만 삭제 가능합니다.");
        }

        return board.getId();
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
