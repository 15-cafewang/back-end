package com.sparta.backend.domain.recipe;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = "recipe")
@Getter
@NoArgsConstructor
@Entity
public class RecipeImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(length = 600)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeImage(String image, Recipe recipe){
        this.image = image;
        this.recipe = recipe;

//        addRecipeImage(this);
    }
//    public void addRecipeImage(RecipeImage recipeImage){
//        recipe.getRecipeImagesList().add(recipeImage);
//    }

    public RecipeImage updateRecipeImage(String image, Recipe recipe){
        this.image = image;
        this.recipe = recipe;
        return this;
    }
}
