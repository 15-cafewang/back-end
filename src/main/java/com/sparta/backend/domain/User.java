package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeComment;
import com.sparta.backend.domain.Recipe.RecipeLikes;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true)
    private Long kakaoId;

    private String image;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Recipe> recipeList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeComment> recipeCommentList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeLikes> recipeLikesList;

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Follow> fromUserFollowList;

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Follow> toUserFollowList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Review> reviewList;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User(String email, String password, String nickname, Long kakaoId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.kakaoId = kakaoId;
    }
}
