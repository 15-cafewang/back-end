package com.sparta.backend.domain.board;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.backend.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.sparta.backend.validator.BoardImageValidator.boardImageValidator;

@Getter
@NoArgsConstructor
@Entity
public class BoardImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "board_id")
    private Board board;

    public BoardImage(String image, Board board) {
        boardImageValidator(image, board);
        this.image = image;
        this.board = board;
    }

    public void updateImage(String image) {
        this.image = image;
    }
}
