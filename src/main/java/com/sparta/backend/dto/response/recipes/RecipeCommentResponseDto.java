package com.sparta.backend.dto.response.recipes;

import com.sparta.backend.domain.Recipe.RecipeComment;
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

    public RecipeCommentResponseDto(RecipeComment recipeComment){
        this.commentId = recipeComment.getId();
        //todo: user이름으로 해야 함
//        this.nickname = comment.getUser().getNickname();
        this.nickname = "this is mock name";
        this.content = recipeComment.getContent();
        this.regdate = recipeComment.getRegDate();
    }
}
