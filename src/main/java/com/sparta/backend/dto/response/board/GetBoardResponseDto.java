package com.sparta.backend.dto.response.board;

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
}
