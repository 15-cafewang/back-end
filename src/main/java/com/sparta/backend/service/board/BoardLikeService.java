package com.sparta.backend.service.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.board.BoardLike;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.board.BoardLikeRepository;
import com.sparta.backend.repository.board.BoardRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;

    //게시물 좋아요/취소
    @Transactional
    public String likeBoard(Long id, UserDetailsImpl userDetails) {

            User user = userDetails.getUser();

            Board board = boardRepository.findById(id).orElseThrow(
                    () -> new CustomErrorException("해당 게시물을 찾을 수 없습니다")
            );

            BoardLike boardLike = boardLikeRepository.findByBoardAndUser(board, user);

            if(boardLike != null) {
                boardLikeRepository.deleteByBoardAndUser(board, user);
                return "게시물에 좋아요 취소하였습니다.";
            } else {
                BoardLike newBoardLike = new BoardLike(user, board);
                boardLikeRepository.save(newBoardLike);
                return "게시물에 좋아요 하였습니다.";
            }
    }
}
