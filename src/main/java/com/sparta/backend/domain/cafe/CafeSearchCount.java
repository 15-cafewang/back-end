package com.sparta.backend.domain.cafe;

import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.user.User;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class CafeSearchCount extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    String keyword;

    public CafeSearchCount(User user, String keyword){
        this.user = user;
        this.keyword = keyword;
    }
}
