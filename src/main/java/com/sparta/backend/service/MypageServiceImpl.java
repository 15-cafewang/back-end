package com.sparta.backend.service;

import com.sparta.backend.domain.Follow;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.response.mypage.GetMypageResponseDto;
import com.sparta.backend.repository.BoardRepository;
import com.sparta.backend.repository.FollowRepository;
import com.sparta.backend.repository.RecipeRepository;
import com.sparta.backend.repository.UserRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MypageServiceImpl implements MypageService{

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BoardRepository boardRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public GetMypageResponseDto getMypageInfo(UserDetailsImpl userDetails, String nickname) {

        String image;
        int followCount;
        int followingCount;
        String followStatus = "";

        // 조회하는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            image = userDetails.getUser().getImage();
            followCount = followRepository.findAllByFromUser(userDetails.getUser()).size();
            followingCount = followRepository.findAllByToUser(userDetails.getUser()).size();
        } else { // 다른 회원일 때
            User foundUser = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 사용자입니다")
            );
            image = foundUser.getImage();
            followCount = followRepository.findAllByFromUser(foundUser).size();
            followingCount = followRepository.findAllByToUser(foundUser).size();

            Optional<Follow> result = followRepository.findByFromUserAndToUser(userDetails.getUser(), foundUser);
            if (result.isPresent()) {
                followStatus = "Y";
            } else {
                followStatus = "N";
            }
        }

        return new GetMypageResponseDto(image, nickname, followCount, followingCount, followStatus);
    }

    @Override
    public void recipeList() {

    }

    @Override
    public void boardList() {

    }

    @Override
    public void likedRecipeList() {

    }

    @Override
    public void likedBoardList() {

    }
}
