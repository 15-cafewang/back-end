package com.sparta.backend.dto.response.cafe;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class CafeDetailResponsetDto {
    private Long cafeId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private int likeCount;
    private boolean likeStatus;
    private List<String> images;
    private List<String> tags;
    private String location;
    private String profile;
    private int rankingStatus;
}
