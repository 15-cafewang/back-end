package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeComment;
import com.sparta.backend.domain.Recipe.RecipeLikes;
import com.sparta.backend.validator.UserValidator;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

import static com.sparta.backend.validator.UserValidator.*;

//@ToString
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

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(unique = true)
    private Long kakaoId;

    private String image;

    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Recipe> recipeList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeComment> recipeCommentList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<RecipeLikes> recipeLikesList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Board> boardList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BoardComment> boardCommentList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BoardLikes> boardLikesList;

    @OneToMany(mappedBy = "fromUser")
    @JsonBackReference
    private List<Follow> fromUserFollowList;

    @OneToMany(mappedBy = "toUser")
    @JsonBackReference
    private List<Follow> toUserFollowList;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    @Builder
    public User(String email, String password, String nickname, String image, UserRole role, String status) {
        validateEmail(email);
        validateNickname(nickname);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.image = image;
        this.role = role;
        this.status = status;
    }

    //test용- id주입받기 위해
    public User(Long id, String email, String password, String nickname, String image, UserRole role, String status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.image = image;
        this.role = role;
        this.status = status;
    }

    public User(String email, String password, String nickname, String image, UserRole role, Long kakaoId, String status) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.image = image;
        this.role = role;
        this.kakaoId = kakaoId;
        this.status = status;
    }

    // 정보 수정
    public void changeProfile(String nickname, String image) {
        validateNickname(nickname);
        this.nickname = nickname;
        this.image = image;
    }

    // 회원 삭제
    public void deleteUser(String status) {
        this.status = status;
    }
}
