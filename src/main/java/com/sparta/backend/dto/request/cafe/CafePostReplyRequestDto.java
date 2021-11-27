package com.sparta.backend.dto.request.cafe;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CafePostReplyRequestDto {
    private Long commentId;
    private String reply;
}
