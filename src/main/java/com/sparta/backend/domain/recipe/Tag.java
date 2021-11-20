package com.sparta.backend.domain.recipe;

import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.recipe.Recipe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = "recipe")
@Getter
@NoArgsConstructor
@Entity
public class Tag extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public Tag(String name, Recipe recipe){
        this.name = name;
        this.recipe = recipe;
    }
}