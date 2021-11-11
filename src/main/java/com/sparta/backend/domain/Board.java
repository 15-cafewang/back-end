package com.sparta.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.dto.request.board.PostBoardRequestDto;
import com.sparta.backend.validator.BoardValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@ToString(exclude = {"user"})
@Getter
@NoArgsConstructor
@Entity
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<BoardImage> boardImageList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BoardComment> boardCommentList;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BoardLikes> boardLikesList;

    public Board(PostBoardRequestDto requestDto, User user) {
        BoardValidator.boardValidatorRequestDto(requestDto, user);
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.user = user;
    }

    //test용 constructor
    public Board(Long id, PostBoardRequestDto requestDto, User user) {
        BoardValidator.boardValidatorBoardId(id, requestDto, user);
        this.id = id;
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.user = user;
    }

    public Board updateBoard(String title, String content) {
        this.title = title;
        this.content = content;

        return this;
    }
}
