package com.sparta.backend.dto.request.recipe;

import lombok.Data;

@Data
public class PostCommentRequestDto {
    private Long recipeId;
    private String content;
}
