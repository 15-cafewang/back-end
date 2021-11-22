package com.sparta.backend.dto.response.board;

import com.sparta.backend.domain.board.BoardComment;
import com.sparta.backend.domain.board.BoardCommentLike;
import com.sparta.backend.repository.board.BoardCommentLikesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetBoardCommentResponseDto {
    private Long commentId;
    private Long boardId;
    private String nickname;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private String profile;
    private String content;
    private int likeCount;
    private boolean likeStatus;

    public GetBoardCommentResponseDto(BoardComment boardComment,
                                      BoardCommentLikesRepository boardCommentLikesRepository,
                                      UserDetailsImpl userDetails) {
        this.commentId = boardComment.getId();
        this.boardId = boardComment.getBoard().getId();
        this.nickname = boardComment.getUser().getNickname();
        this.regDate = boardComment.getRegDate();
        this.modDate = boardComment.getModDate();
        this.profile = boardComment.getUser().getImage();
        this.content = boardComment.getContent();
        this.likeCount = boardComment.getBoardCommentLikeList() == null ?
                                                    0 : boardComment.getBoardCommentLikeList().size();
        BoardCommentLike boardCommentLikeList =
                boardCommentLikesRepository.findByBoardCommentAndUser(boardComment, userDetails.getUser());
        this.likeStatus = boardCommentLikeList != null;

    }
}
