package com.sparta.backend.dto.response.recipes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class RecipeDetailResponsetDto {
    private Long recipeId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime regdate;
    private int likeCount;
    private boolean likeStatus;
    private List<String> images;
    private List<String> tags;
}
