package com.sparta.backend.domain.board;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
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

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1500)
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
    private List<BoardLike> boardLikeList;

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
