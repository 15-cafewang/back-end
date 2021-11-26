package com.sparta.backend.domain.cafe;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.validator.CafeCommentValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString(exclude = {"user", "cafe"})
@Getter
@NoArgsConstructor
@Entity
public class CafeComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_comment_id")
    private Long id;

    @Column(nullable = false, length = 1500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    @OneToMany(mappedBy = "cafeComment", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CafeCommentLike> commentLikes;

    public CafeComment(String content, User user, Cafe cafe) {
        CafeCommentValidator.validateCommentInput(content, user, cafe);
        this.content = content;
        this.user = user;
        this.cafe = cafe;
    }

    public CafeComment updateComment(String content){
        CafeCommentValidator.validateCommentInput(content);
        this.content = content;
        return this;
    }
}
