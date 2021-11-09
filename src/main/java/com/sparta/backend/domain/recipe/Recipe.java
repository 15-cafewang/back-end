package com.sparta.backend.domain.recipe;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.Tag;
import com.sparta.backend.domain.User;
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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private Integer price;

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
    private List<RecipeLikes> recipeLikesList;

    @OneToMany(mappedBy = "recipe",  cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<RecipeImage> recipeImagesList;

    public Recipe(String title, String content, Integer price, User user){
        //Edge케이스들 validation
//        RecipeValidator.validateRecipeInput(title,content,user);
        this.title = title;
        this.content = content;
        this.price = price;
        this.user = user;
    }
    public Recipe updateRecipe(String title, String content, Integer price) {
        this.title = title;
        this.content = content;
        this.price = price;

        return this;
    }

    //test용(강제 id주입 위해)
    public Recipe(Long id, String title, String content, Integer price, User user){
        this.id = id;
        this.title = title;
        this.content = content;
        this.price = price;
        this.user = user;
    }
}
