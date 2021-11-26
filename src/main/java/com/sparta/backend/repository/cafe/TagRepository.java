package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findAllByCafe(Cafe cafe);
    void deleteAllByCafe(Cafe cafe);

    @Query("select t from CafeDetailCount dc, Tag t where dc.user.id = :userId and dc.cafe = t.cafe group by t.name order by count(t.name) desc ")
    List<Tag> findRecommendedTag(@Param("userId") Long userId);
}
