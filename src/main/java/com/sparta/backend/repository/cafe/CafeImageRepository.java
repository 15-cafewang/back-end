package com.sparta.backend.repository.cafe;

import com.sparta.backend.domain.cafe.CafeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CafeImageRepository extends JpaRepository<CafeImage,Long> {
//    @Query("delete from RecipeImage i where i.image in :imageUrls")
    void deleteByImageIn(List<String> imageUrls);

    List<CafeImage> findByImageIn(List<String> imageUrls);
}
