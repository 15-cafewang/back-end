package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private List<Comment> commentList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Likes> likesList;

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
    public User(String email, String password, String nickname, String image, UserRole role, String status) {
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
        this.nickname = nickname;
        this.image = image;
    }

    // 회원 삭제
    public void deleteUser(String status) {
        this.status = status;
    }
}
