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
}
