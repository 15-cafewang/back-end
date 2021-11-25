package com.sparta.backend.domain.cafe;

import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class CafeDetailCount extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    public CafeDetailCount(User user, Cafe cafe){
        this.user = user;
        this.cafe = cafe;
    }
}
