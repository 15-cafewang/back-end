package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.CafeSearchCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeSearchCountRepository extends JpaRepository<CafeSearchCount,Long> {
}
