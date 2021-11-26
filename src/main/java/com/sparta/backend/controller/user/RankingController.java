package com.sparta.backend.controller.user;

import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.user.GetThisWeekRankingResponseDto;
import com.sparta.backend.dto.response.user.GetKingsLastWeekResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.user.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/main/thisweek")
    public ResponseEntity<?> getThisWeekRanking(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);
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

    //저번 주 왕 된 유저들 조회
    @GetMapping("/main/lastweek")
    public ResponseEntity<?> getKingsLastWeek(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        checkLogin(userDetails);
        GetKingsLastWeekResponseDto responseDtoList = rankingService.getKingsLastWeek();

        if(responseDtoList != null) {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(1, "저번 주 각 부문 왕들 조회 성공", responseDtoList),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new CustomResponseDto<>(-1, "저번 주 각 부문 왕들 조회 실패", ""),
                    HttpStatus.BAD_REQUEST);
        }

    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }
}
