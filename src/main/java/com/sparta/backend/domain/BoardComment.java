package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.dto.request.board.PostBoardCommentRequestDto;
import com.sparta.backend.dto.request.board.PutBoardCommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString(exclude = {"user", "board"})
@Getter
@NoArgsConstructor
@Entity
public class BoardComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false, length = 1500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "boardComment", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BoardCommentLikes> boardCommentLikesList;

    public BoardComment(PostBoardCommentRequestDto requestDto, Board board, User user) {
        this.content = requestDto.getContent();
        this.board = board;
        this.user = user;
    }

    public void updateComment(PutBoardCommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
