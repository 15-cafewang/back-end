package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.CafeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CafeCommentRepository extends JpaRepository<CafeComment,Long> {
    Page<CafeComment> findAllByCafeId(Long cafeId, Pageable pageable);

}
