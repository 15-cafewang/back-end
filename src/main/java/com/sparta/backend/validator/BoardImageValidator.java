package com.sparta.backend.validator;

import com.sparta.backend.domain.board.Board;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class BoardImageValidator {
    public static void boardImageValidator(String image, Board board) {
        //이미지 URL
        if(image == null || image.equals("")) {
            throw new NullPointerException("이미지가 존재하지 않습니다.");
        } else {
            try {
                new URL(image).toURI();
            } catch(MalformedURLException | URISyntaxException e) {
                throw new IllegalArgumentException("URL 형식이 아닙니다.");
            }
        }

        //게시물
        if(board == null) {
            throw new NullPointerException("존재하지 않는 게시물입니다.");
        }
        // Board 엔티티에서 아래와 같은 값이 들어온다면
        // BoardComment 엔티티에서 exception이 나기 전
        // BoardValidator에서 exception이 일어남
//        else {
//            if(board.getId() == null || board.getId() <= 0) {
//                throw new NullPointerException("존재하지 않는 게시물입니다");
//            }
//        }
    }
}
