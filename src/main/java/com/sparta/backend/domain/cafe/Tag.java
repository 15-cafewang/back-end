package com.sparta.backend.domain.cafe;

import com.sparta.backend.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(exclude = "cafe")
@Getter
@NoArgsConstructor
@Entity
public class Tag extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    public Tag(String name, Cafe cafe){
        this.name = name;
        this.cafe = cafe;
    }
}
