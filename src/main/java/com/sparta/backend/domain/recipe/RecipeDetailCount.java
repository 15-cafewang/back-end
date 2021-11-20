package com.sparta.backend.domain.recipe;

import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class RecipeDetailCount extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeDetailCount(User user,Recipe recipe){
        this.user = user;
        this.recipe = recipe;
    }
}
