package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = {"user", "boardComment"})
@Getter
@NoArgsConstructor
@Entity
public class BoardCommentLikes extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "board_comment_id")
    private BoardComment boardComment;

    public BoardCommentLikes(User user, BoardComment boardComment) {
        this.user = user;
        this.boardComment = boardComment;
    }
}
