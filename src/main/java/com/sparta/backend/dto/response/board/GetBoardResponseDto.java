package com.sparta.backend.dto.response.board;

import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.repository.board.BoardLikeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetBoardResponseDto {
    private Long boardId;
    private String nickname;
    private String title;
    private String content;
    private String image;
    private LocalDateTime regDate;
    private int commentCount;
    private int likeCount;
    private boolean likeStatus;

    public GetBoardResponseDto(Board board, User currentLoginUser, BoardLikeRepository boardLikeRepository) {
        this.boardId = board.getId();
        this.nickname = board.getUser().getNickname();
        this.title = board.getTitle();
        this.content = board.getContent();
        //이미지가 있을 경우
        if(board.getBoardImageList().size() > 0 && board.getBoardImageList() != null)
            this.image = board.getBoardImageList().get(0).getImage();
        else    //이미지가 없을 경우
            this.image = null;
        this.regDate = board.getRegDate();
        this.commentCount = board.getBoardCommentList().size();
        this.likeCount = board.getBoardLikeList().size();
        this.likeStatus = boardLikeRepository.findByBoardAndUser(board, currentLoginUser) != null;
    }
}
