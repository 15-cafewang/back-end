package com.sparta.backend.domain.cafe;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = "cafe")
@Getter
@NoArgsConstructor
@Entity
public class CafeImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(length = 600)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    public CafeImage(String image, Cafe cafe){
        this.image = image;
        this.cafe = cafe;

//        addRecipeImage(this);
    }
//    public void addRecipeImage(RecipeImage recipeImage){
//        recipe.getRecipeImagesList().add(recipeImage);
//    }

    public CafeImage updateCafeImage(String image, Cafe cafe){
        this.image = image;
        this.cafe = cafe;
        return this;
    }
}
