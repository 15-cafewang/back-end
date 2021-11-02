package com.sparta.backend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;
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
}
