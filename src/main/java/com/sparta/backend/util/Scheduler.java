package com.sparta.backend.util;

import com.sparta.backend.domain.user.User;
import com.sparta.backend.repository.user.UserRepository;
import com.sparta.backend.service.user.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class Scheduler {

    private final RankingService rankingService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 1 0 ? * MON")
    @Transactional
    public void setupRankingData() {

        // 1. 모든 유저의 rankingStatus 0으로 초기화
        userRepository.initRankingStatus();

        // 2. 각각 왕들의 rankingStatus 변경
        // 1 == 좋아요왕
        if (rankingService.getMostLikes() != null) {
            Optional<User> likeKing = userRepository.findByNickname(rankingService.getMostLikes().getNickname());
            likeKing.ifPresent(user -> user.changeRankingStatus(1));
        }
        // 2 == 게시글왕
        if (rankingService.getMostWrotePosts() != null) {
            Optional<User> postKing = userRepository.findByNickname(rankingService.getMostWrotePosts().getNickname());
            postKing.ifPresent(user -> user.changeRankingStatus(2));
        }
        // 3 == 팔로우왕
        if (rankingService.getMostFollows() != null) {
            Optional<User> followKing = userRepository.findByNickname(rankingService.getMostFollows().getNickname());
            followKing.ifPresent(user -> user.changeRankingStatus(3));
        }
        // 4 == 댓글왕
        if (rankingService.getMostWroteComments() != null) {
            Optional<User> commentKing = userRepository.findByNickname(rankingService.getMostWroteComments().getNickname());
            commentKing.ifPresent(user -> user.changeRankingStatus(4));
        }

    }
}
