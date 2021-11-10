package com.sparta.backend.dto.response.recipes;

import com.sparta.backend.domain.Recipe.RecipeComment;
import com.sparta.backend.domain.Recipe.RecipeCommentLikes;
import com.sparta.backend.repository.RecipeCommentLikeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
public class RecipeCommentResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime regDate;
    private int likeCount;
    private boolean likeStatus;
    private String profileImage;

    public RecipeCommentResponseDto(RecipeComment recipeComment, UserDetailsImpl userDetails, RecipeCommentLikeRepository commentLikeRepository){
        this.commentId = recipeComment.getId();
        this.nickname = recipeComment.getUser().getNickname();
        this.content = recipeComment.getContent();
        this.regDate = recipeComment.getRegDate();
        this.likeCount = recipeComment.getCommentLikes().size();

        Optional<RecipeCommentLikes> foundCommentLikes = commentLikeRepository.findByRecipeCommentAndUser(recipeComment,userDetails.getUser());
        this.likeStatus = foundCommentLikes.isPresent();
        profileImage = userDetails.getUser().getImage();
    }
}
