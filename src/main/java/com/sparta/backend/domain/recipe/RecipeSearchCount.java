package com.sparta.backend.domain.recipe;

import com.sparta.backend.domain.BaseEntity;
import com.sparta.backend.domain.User;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
public class RecipeSearchCount extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    String keyword;

    public RecipeSearchCount(User user, String keyword){
        this.user = user;
        this.keyword = keyword;
    }
}
