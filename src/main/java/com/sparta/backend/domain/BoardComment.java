package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = {"user", "board"})
@Getter
@NoArgsConstructor
@Entity
public class BoardComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardComment(PostBoardCommentRequestDto requestDto, Board board, User user) {
        this.content = requestDto.getContent();
        this.board = board;
        this.user = user;
    }

}
