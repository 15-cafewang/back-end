package com.sparta.backend.dto.response.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetBoardDetailResponseDto {
    private Long boardId;
    private String title;
    private String nickname;
    private String profile;
    private LocalDateTime regDate;
    private String content;
    private List<String> images;
    private int likeCount;
    private boolean likeStatus;
}
