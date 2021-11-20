package com.sparta.backend.domain.recipe;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.validator.RecipeCommentValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString(exclude = {"user", "recipe"})
@Getter
@NoArgsConstructor
@Entity
public class RecipeComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, length = 1500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @OneToMany(mappedBy = "recipeComment", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeCommentLike> commentLikes;

    public RecipeComment(String content, User user, Recipe recipe) {
        RecipeCommentValidator.validateCommentInput(content, user, recipe);
        this.content = content;
        this.user = user;
        this.recipe = recipe;
    }

    public RecipeComment updateComment(String content){
        RecipeCommentValidator.validateCommentInput(content);
        this.content = content;
        return this;
    }
}
