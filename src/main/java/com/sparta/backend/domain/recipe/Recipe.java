package com.sparta.backend.domain.recipe;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.validator.RecipeValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString(exclude = {"user"})
@Getter
@NoArgsConstructor
@Entity
public class Recipe extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1500)
    private String content;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recipe",cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Tag> tagList;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeComment> recipeCommentList;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeLike> recipeLikeList;

    @OneToMany(mappedBy = "recipe",  cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<RecipeImage> recipeImagesList;

    @OneToMany(mappedBy = "recipe",  cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<RecipeDetailCount> recipeDetailCountList;

    public Recipe(String title, String content, String locatioin, User user){
        //Edge케이스들 validation
        RecipeValidator.validateRecipeInput(title,content, locatioin,user);
        this.title = title;
        this.content = content;
        this.location = locatioin;
        this.user = user;
    }
    public Recipe updateRecipe(String title, String content, String location) {
        this.title = title;
        this.content = content;
        this.location = location;

        return this;
    }

    //test용(강제 id주입 위해)
    public Recipe(Long id, String title, String content, String locatioin, User user){
        this.id = id;
        this.title = title;
        this.content = content;
        this.location = locatioin;
        this.user = user;
    }
}
