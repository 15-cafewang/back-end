package com.sparta.backend.dto.request.user;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserRequestDto {

    private String nickname;
    private MultipartFile image;
}
