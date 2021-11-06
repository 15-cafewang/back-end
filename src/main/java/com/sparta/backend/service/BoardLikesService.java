package com.sparta.backend.service;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardLikes;
import com.sparta.backend.domain.User;
import com.sparta.backend.repository.BoardLikesRepository;
import com.sparta.backend.repository.BoardRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BoardLikesService {

    private final BoardLikesRepository boardLikesRepository;
    private final BoardRepository boardRepository;

    //게시물 좋아요/취소
    @Transactional
    public String likeBoard(Long id, UserDetailsImpl userDetails) {

        if(userDetails != null) {
            User user = userDetails.getUser();

            Board board = boardRepository.findById(id).orElseThrow(
                    () -> new NullPointerException("찾는 게시물이 없습니다.")
            );

            BoardLikes boardLikes = boardLikesRepository.findByBoardAndUser(board, user);

            if(boardLikes != null) {
                boardLikesRepository.deleteByBoardAndUser(board, user);
                return "게시물에 좋아요 취소하였습니다.";
            } else {
                BoardLikes newBoardLikes = new BoardLikes(user, board);
                boardLikesRepository.save(newBoardLikes);
                return "게시물에 좋아요 하였습니다.";
            }

        } else {
            throw new NullPointerException("로그인이 필요합니다.");
        }
    }
}
