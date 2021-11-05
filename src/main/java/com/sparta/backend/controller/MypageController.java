package com.sparta.backend.controller;

import com.sparta.backend.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;
}
