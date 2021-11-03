package com.sparta.backend.domain.Recipe;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = {"user", "recipe"})
@Getter
@NoArgsConstructor
@Entity
public class RecipeComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeComment(String content, User user, Recipe recipe) {
        this.content = content;
        this.user = user;
        this.recipe = recipe;
    }

    public void updateComment(String content){
        this.content = content;
    }
}
