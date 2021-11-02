package com.sparta.backend.dto.request.user;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateRequestDto {

    private String nickname;
    private MultipartFile image;
}
