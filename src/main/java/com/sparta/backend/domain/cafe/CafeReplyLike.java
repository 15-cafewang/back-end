package com.sparta.backend.domain.cafe;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class CafeReplyLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cafe_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "cafe_reply_id")
    private CafeReply cafeReply;
}
