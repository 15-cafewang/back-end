package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeTag> recipeTagList;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Comment> commentList;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Likes> likesList;
}
