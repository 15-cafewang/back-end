package com.sparta.backend.dto.response.recipes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecipeCommentResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime regdate;
}
