package com.sparta.backend.dto.queryInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendUserDataCheckDto {
    Boolean existLike;
    Boolean existSearch;
    Boolean existDetail;
}
