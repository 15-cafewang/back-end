package com.sparta.backend.controller;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.GetThisWeekRankingResponseDto;
import com.sparta.backend.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/thisweek")
    public ResponseEntity<?> getThisWeekRanking() {

        List<List<GetThisWeekRankingResponseDto>> list = new ArrayList<>();

        List<GetThisWeekRankingResponseDto> top3ByMostLiked = rankingService.getTop3ByMostLiked();
        List<GetThisWeekRankingResponseDto> top3ByMostRecipe = rankingService.getTop3ByMostRecipe();
        List<GetThisWeekRankingResponseDto> top3ByMostFollow = rankingService.getTop3ByMostFollow();
        List<GetThisWeekRankingResponseDto> top3ByMostComment = rankingService.getTop3ByMostComment();

        list.add(top3ByMostLiked);
        list.add(top3ByMostRecipe);
        list.add(top3ByMostFollow);
        list.add(top3ByMostComment);

        return new ResponseEntity<>(
                new CustomResponseDto<>(1, "실시간 랭킹 조회 성공", list), HttpStatus.OK);
    }
}
