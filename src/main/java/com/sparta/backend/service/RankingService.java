package com.sparta.backend.service;

import com.sparta.backend.dto.response.GetThisWeekRankingResponseDto;
import com.sparta.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import com.sparta.backend.dto.response.user.GetKingUserInfoLastWeekResponseDto;
import com.sparta.backend.dto.response.user.GetKingsLastWeekResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Transactional
@RequiredArgsConstructor
@Service
public class RankingService {

    private final UserRepository userRepository;

    LocalDateTime time = LocalDateTime.now();
    LocalDateTime monday = time.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

    public List<GetThisWeekRankingResponseDto> getTop3ByMostLiked() {

        List<Object[]> top3ByMostLiked = userRepository.findTop3ByMostLiked(monday, time);

        return top3ByMostLiked.stream().map(like -> new GetThisWeekRankingResponseDto(
                (String) like[0],
                (String) like[1],
                ((BigInteger) like[2]).longValue()
        )).collect(Collectors.toList());
    }

    public List<GetThisWeekRankingResponseDto> getTop3ByMostRecipe() {

        List<Object[]> top3ByMostRecipe = userRepository.findTop3ByMostRecipe(monday, time);

        return top3ByMostRecipe.stream().map(recipe -> new GetThisWeekRankingResponseDto(
                (String) recipe[0],
                (String) recipe[1],
                ((BigInteger) recipe[2]).longValue()
        )).collect(Collectors.toList());
    }

    public List<GetThisWeekRankingResponseDto> getTop3ByMostFollow() {

        List<Object[]> top3ByMostFollow = userRepository.findTop3ByMostFollow(monday, time);

        return top3ByMostFollow.stream().map(follow -> new GetThisWeekRankingResponseDto(
                (String) follow[0],
                (String) follow[1],
                ((BigInteger) follow[2]).longValue()
        )).collect(Collectors.toList());
    }

    public List<GetThisWeekRankingResponseDto> getTop3ByMostComment() {

        List<Object[]> top3ByMostComment = userRepository.findTop3ByMostComment(monday, time);

        return top3ByMostComment.stream().map(comment -> new GetThisWeekRankingResponseDto(
                (String) comment[0],
                (String) comment[1],
                ((BigInteger) comment[2]).longValue()
        )).collect(Collectors.toList());
    }

    /*
    [공통]
    지난주 데이터가 아예 없을 때 : 빈 리스트로 리턴
    각 항목에서 공동 1등이 나왔을 때 : 먼저 가입한 사람이 우선
    */

    public GetKingsLastWeekResponseDto getKingsLastWeek() {

        GetKingsLastWeekResponseDto responseDto =
                new GetKingsLastWeekResponseDto(getMostLikes(), getMostFollows(),
                                                getMostWrotePosts(), getMostWroteComments());

        return responseDto;

    }

    //저번 주에 카페 게시물 좋아요 가장 많이 받은 1인
    public GetKingUserInfoLastWeekResponseDto getMostLikes() {
        List<LocalDateTime> startEndDate = getStartDateAndEndDate();

        //get(0) : lastMonday, get(1) : lastSunday
        List<Object[]> theMostLikedUserList =
                userRepository.findTheMostLikedUser(startEndDate.get(0), startEndDate.get(1));

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
                userRepository.findTheMostWrotePostsUser(startEndDate.get(0), startEndDate.get(1));

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
                userRepository.findTheMostWroteCommentsUser(startEndDate.get(0), startEndDate.get(1));

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
