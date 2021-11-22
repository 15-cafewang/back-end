package com.sparta.backend.service.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.board.BoardLike;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.board.BoardLikesRepository;
import com.sparta.backend.repository.board.BoardRepository;
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

            User user = userDetails.getUser();

            Board board = boardRepository.findById(id).orElseThrow(
                    () -> new CustomErrorException("해당 게시물을 찾을 수 없습니다")
            );

            BoardLike boardLike = boardLikesRepository.findByBoardAndUser(board, user);

            if(boardLike != null) {
                boardLikesRepository.deleteByBoardAndUser(board, user);
                return "게시물에 좋아요 취소하였습니다.";
            } else {
                BoardLike newBoardLike = new BoardLike(user, board);
                boardLikesRepository.save(newBoardLike);
                return "게시물에 좋아요 하였습니다.";
            }
    }
}
