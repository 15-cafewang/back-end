package com.sparta.backend.controller.user;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.user.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follows/{nickname}")
    public ResponseEntity<?> follow(@PathVariable String nickname,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {

        followService.follow(userDetails, nickname);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "팔로우 성공", ""),
                HttpStatus.OK);
    }

    @DeleteMapping("/follows/{nickname}")
    public ResponseEntity<?> unFollow(@PathVariable String nickname,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        followService.unFollow(userDetails, nickname);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "언팔로우 성공", ""),
                HttpStatus.OK);
    }
}
