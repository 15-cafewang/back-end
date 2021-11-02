package com.sparta.backend.dto.response.recipes;

import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.RecipeComment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecipeListResponseDto {
    private Long recipeId;
    private String nickname;
    private String content;
    private LocalDateTime regdate;
    private int likeCount;
    private boolean likeStatus;

    public RecipeListResponseDto(Recipe recipe){
        this.recipeId = recipe.getId();
        //todo: user이름으로 해야 함
//        this.nickname = comment.getUser().getNickname();
        this.nickname = "this is mock name";
        this.content = recipe.getTitle();
        this.regdate = recipe.getRegDate();

        //todo:likeCount, likeStatus구하는 코드 구해서 likeCount에 대입.현재 임시 값임.
        this.likeCount = 200;
        this.likeStatus = true;
    }
}