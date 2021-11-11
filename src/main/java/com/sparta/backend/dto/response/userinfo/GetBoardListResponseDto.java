package com.sparta.backend.dto.response.userinfo;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.BoardLikes;
import com.sparta.backend.repository.BoardLikesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetBoardListResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private int likeCount;
    private int commentCount;
    private List<String> imageList = new ArrayList<>();
    private boolean likeStatus;

    public GetBoardListResponseDto(Board board,
                                   UserDetailsImpl userDetails,
                                   BoardLikesRepository boardLikesRepository) {

        this.boardId = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.regDate = board.getRegDate();
        this.likeCount = board.getBoardLikesList().size();
        this.commentCount = board.getBoardCommentList().size();
        board.getBoardImageList().forEach((boardImage -> this.imageList.add(boardImage.getImage())));

        Optional<BoardLikes> foundBoardLike = boardLikesRepository
                .findByBoardIdAndUserId(board.getId(), userDetails.getUser().getId());
        this.likeStatus = foundBoardLike.isPresent();
    }

}
