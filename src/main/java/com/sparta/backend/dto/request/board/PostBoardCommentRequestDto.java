package com.sparta.backend.dto.request.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostBoardCommentRequestDto {
    private Long boardId;
    private String content;
}
