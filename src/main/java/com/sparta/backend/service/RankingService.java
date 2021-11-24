package com.sparta.backend.service;

import com.sparta.backend.dto.response.user.GetKingUserInfoLastWeekResponseDto;
import com.sparta.backend.dto.response.user.GetKingsLastWeekResponseDto;
import com.sparta.backend.repository.recipe.RecipeCommentRepository;
import com.sparta.backend.repository.recipe.RecipeLikesRepository;
import com.sparta.backend.repository.recipe.RecipeRepository;
import com.sparta.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class RankingService {

    private final RecipeLikesRepository recipeLikesRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeCommentRepository recipeCommentRepository;

    /*
    [공통]
    지난주 데이터가 아예 없을 때 : 빈 리스트로 리턴
    각 항목에서 공동 1등이 나왔을 때 : 먼저 가입한 사람이 우선
    */

    public List<GetKingsLastWeekResponseDto> getKingsLastWeek() {
        List<GetKingsLastWeekResponseDto> responseDtoList = new ArrayList<>();

        GetKingsLastWeekResponseDto responseDto =
                new GetKingsLastWeekResponseDto(getMostLikes(), getMostFollows(),
                                                getMostWrotePosts(), getMostWroteComments());
        responseDtoList.add(responseDto);

        return responseDtoList;

    }

    //저번 주에 카페 게시물 좋아요 가장 많이 받은 1인
    public GetKingUserInfoLastWeekResponseDto getMostLikes() {
        List<LocalDateTime> startEndDate = getStartDateAndEndDate();

        //get(0) : lastMonday, get(1) : lastSunday
        List<Object[]> theMostLikedUserList =
                recipeLikesRepository.findTheMostLikedUser(startEndDate.get(0), startEndDate.get(1));

        GetKingUserInfoLastWeekResponseDto responseDto = null;
        if(theMostLikedUserList != null && theMostLikedUserList.size() > 0) {
            Object[] object = theMostLikedUserList.get(0);

            responseDto = new GetKingUserInfoLastWeekResponseDto(object);
        }

        return responseDto;
    }

    //저번 주에 팔로우를 가장 많이 받은 1인
    public GetKingUserInfoLastWeekResponseDto getMostFollows() {
        List<LocalDateTime> startEndDate = getStartDateAndEndDate();

        //get(0) : lastMonday, get(1) : lastSunday
        List<Object[]> theMostFollowedUserList =
                userRepository.findTheMostFollowedUser(startEndDate.get(0), startEndDate.get(1));

        GetKingUserInfoLastWeekResponseDto responseDto = null;
        if(theMostFollowedUserList != null && theMostFollowedUserList.size() > 0) {
            Object[] object = theMostFollowedUserList.get(0);

            responseDto = new GetKingUserInfoLastWeekResponseDto(object);
        }

        return responseDto;
    }

    //저번 주에 카페 게시물을 가장 많이 게시한 1인
    public GetKingUserInfoLastWeekResponseDto getMostWrotePosts() {
        List<LocalDateTime> startEndDate = getStartDateAndEndDate();

        //get(0) : lastMonday, get(1) : lastSunday
        List<Object[]> theMostWritePostsUserList =
                recipeRepository.findTheMostWrotePostsUser(startEndDate.get(0), startEndDate.get(1));

        GetKingUserInfoLastWeekResponseDto responseDto = null;
        if(theMostWritePostsUserList != null && theMostWritePostsUserList.size() > 0) {
            Object[] object = theMostWritePostsUserList.get(0);

            responseDto = new GetKingUserInfoLastWeekResponseDto(object);
        }

        return responseDto;
    }

    //저번 주에 카페 게시물 댓글을 가장 많이 단 1인
    public GetKingUserInfoLastWeekResponseDto getMostWroteComments() {
        List<LocalDateTime> startEndDate = getStartDateAndEndDate();

        //get(0) : lastMonday, get(1) : lastSunday
        List<Object[]> theMostWriteCommentsUserList =
                recipeCommentRepository.findTheMostWroteCommentsUser(startEndDate.get(0), startEndDate.get(1));

        GetKingUserInfoLastWeekResponseDto responseDto = null;
        if(theMostWriteCommentsUserList != null && theMostWriteCommentsUserList.size() > 0) {
            Object[] object = theMostWriteCommentsUserList.get(0);

            responseDto = new GetKingUserInfoLastWeekResponseDto(object);
        }

        return responseDto;
    }

    //저번 주 월요일 & 저번주 일요일 구하는 함수
    public List<LocalDateTime> getStartDateAndEndDate() {
        List<LocalDateTime> startEnd = new ArrayList<>();

        //DB에 데이터를 쌓은 지 일주일이 지나야 저번 주 왕에 대한 데이터가 나옴
        //LocalDateTime now = LocalDateTime.now();

        //개발 시 데이터가 조회되지 않으면 불편하기 때문에
        //데이터가 조회되도록 now 변수의 날짜를 조작해놓음
        String dateStr = "2021-12-01 10:36:02";    //테스트 데이터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.parse(dateStr, formatter);

        //저번 주 월요일 : 지난갔던 가장 최근의 월요일에서 7일 빼기
        LocalDateTime lastMonday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusDays(7)
                                      .withHour(0).withMinute(0).withSecond(0);
        startEnd.add(lastMonday);

        //저번 주 일요일 : 지나갔던 가장 최근의 월요일에서 7일 뺀 상태에서 6일 더하기
        LocalDateTime lastSunday = lastMonday.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        startEnd.add(lastSunday);

        return startEnd;
    }
}
