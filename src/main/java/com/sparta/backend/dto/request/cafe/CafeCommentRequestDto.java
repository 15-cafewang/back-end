package com.sparta.backend.dto.request.cafe;

import lombok.Data;

@Data
public class CafeCommentRequestDto {
    private Long cafeId;
    private String content;
}
