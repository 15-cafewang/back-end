package com.sparta.backend.domain.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.board.Board;
import com.sparta.backend.domain.board.BoardComment;
import com.sparta.backend.domain.board.BoardLike;
import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.CafeComment;
import com.sparta.backend.domain.cafe.CafeLike;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

import static com.sparta.backend.validator.UserValidator.*;

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

    @Column(length = 600)
    private String image;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "int default 0")
    private int rankingStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Cafe> cafeList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CafeComment> cafeCommentList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CafeLike> cafeLikeList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Board> boardList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BoardComment> boardCommentList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BoardLike> boardLikeList;

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

    public void changeProfile(String nickname, String image) {
        validateNickname(nickname);
        this.nickname = nickname;
        this.image = image;
    }

    public void changeNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
    }

    public void deleteUser(String email, String nickname, String image) {
        this.email = email;
        this.nickname = nickname;
        this.image = image;
        this.status = "N";
    }

    public void changeRankingStatus(int rankingStatus) {
        this.rankingStatus = rankingStatus;
    }
}
