package com.sparta.backend.service;

import com.sparta.backend.domain.Follow;
import com.sparta.backend.domain.User;
import com.sparta.backend.repository.FollowRepository;
import com.sparta.backend.repository.UserRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    // 팔로우
    public void follow(UserDetailsImpl userDetails, String nickname) {

        // 로그인한 사용자
        User fromUser = userDetails.getUser();

        // 팔로우 신청하려는 사용자
        User toUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다")
        );

        Optional<Follow> foundFollow = followRepository.findByFromUserAndToUser(fromUser, toUser);

        if (foundFollow.isPresent()) {
            throw new IllegalArgumentException("이미 팔로우한 사용자입니다");
        }

        if (userDetails.getUser().getNickname().equals(nickname)) {
            throw new IllegalArgumentException("자기 자신은 팔로우 할 수 없습니다");
        }

        Follow follow = new Follow(fromUser, toUser);

        followRepository.save(follow);
    }

    // 언팔로우
    public void unFollow(UserDetailsImpl userDetails, String nickname) {

        // 로그인한 사용자
        User fromUser = userDetails.getUser();

        // 언팔로우 하려는 사용자
        User toUser = userRepository.findByNickname(nickname).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자입니다")
        );

        Follow foundFollow = followRepository.findByFromUserAndToUser(fromUser, toUser).orElseThrow(
                () -> new NullPointerException("존재하지 않는 팔로우 정보입니다")
        );

        followRepository.deleteById(foundFollow.getId());
    }
}
