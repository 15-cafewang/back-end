package com.sparta.backend.dto.response.recipes;

import com.sparta.backend.domain.recipe.RecipeComment;
import com.sparta.backend.domain.recipe.RecipeCommentLikes;
import com.sparta.backend.repository.recipe.RecipeCommentLikeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
public class RecipeCommentResponseDto {
    private Long commentId;
    private Long recipeId;
    private String nickname;
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int likeCount;
    private boolean likeStatus;
    private String profileImage;

    public RecipeCommentResponseDto(RecipeComment recipeComment, UserDetailsImpl userDetails, RecipeCommentLikeRepository commentLikeRepository){
        this.commentId = recipeComment.getId();
        this.recipeId = recipeComment.getRecipe().getId();
        this.nickname = recipeComment.getUser().getNickname();
        this.content = recipeComment.getContent();
        this.regDate = recipeComment.getRegDate();
        this.modDate = recipeComment.getModDate();
        this.likeCount = recipeComment.getCommentLikes().size();

        Optional<RecipeCommentLikes> foundCommentLikes = commentLikeRepository.findByRecipeCommentAndUser(recipeComment,userDetails.getUser());
        this.likeStatus = foundCommentLikes.isPresent();
        profileImage = userDetails.getUser().getImage();
    }
}
