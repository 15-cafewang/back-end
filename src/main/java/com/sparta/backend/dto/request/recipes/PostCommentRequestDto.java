package com.sparta.backend.dto.request.recipes;

import lombok.Data;

@Data
public class PostCommentRequestDto {
    private Long recipeId;
    private String content;
}
