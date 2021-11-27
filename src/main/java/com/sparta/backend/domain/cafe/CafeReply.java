package com.sparta.backend.domain.cafe;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class CafeReply  extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_reply_id")
    private Long id;

    @Column(nullable = false, length = 1500)
    private String reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "cafe_comment_id")
    private CafeComment cafeComment;

    @OneToMany(mappedBy = "cafeReply", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CafeReplyLike> replyLikes;

    public CafeReply(String reply, User user, CafeComment comment) {
        this.reply = reply;
        this.user = user;
        this.cafeComment = comment;
    }
}
