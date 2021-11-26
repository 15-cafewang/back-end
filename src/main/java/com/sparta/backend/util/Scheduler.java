package com.sparta.backend.util;

import com.sparta.backend.domain.user.User;
import com.sparta.backend.repository.user.UserRepository;
import com.sparta.backend.service.user.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class Scheduler {

    private final RankingService rankingService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 1 0 ? * MON")
    public void setupRankingData() throws InterruptedException {

        // 1. 모든 유저의 rankingStatus 0으로 초기화
        userRepository.initRankingStatus();

        // 2. 각각의 왕들이 rankingStatus 변경
        // 1 == 좋아요왕, 2 == 게시글왕, 3 == 팔로우왕, 4 == 댓글왕
        User likeKing = userRepository.findByNickname(rankingService.getMostLikes().getNickname()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );
        likeKing.changeRankingStatus(1);

        User postKing = userRepository.findByNickname(rankingService.getMostWrotePosts().getNickname()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );
        postKing.changeRankingStatus(2);

        User followKing = userRepository.findByNickname(rankingService.getMostFollows().getNickname()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );
        followKing.changeRankingStatus(3);

        User commentKing = userRepository.findByNickname(rankingService.getMostWroteComments().getNickname()).orElseThrow(
                () -> new NullPointerException("존재하지 않는 회원입니다")
        );
        commentKing.changeRankingStatus(4);
    }
}
