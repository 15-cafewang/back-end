package com.sparta.backend.service;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.Follow;
import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.response.userinfo.*;
import com.sparta.backend.repository.*;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserinfoServiceImpl implements UserinfoService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final RecipeRepository recipeRepository;
    private final BoardRepository boardRepository;

    @Override
    public GetUserinfoResponseDto getUserInfo(UserDetailsImpl userDetails, String nickname) {

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

        return new GetUserinfoResponseDto(image, nickname, followCount, followingCount, followStatus);
    }

    @Override
    public Page<GetRecipeListResponseDto> getRecipeListByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails, String nickname) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        User user;

        // 조회화는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            user = userDetails.getUser();
        } else { // 다른 회원일 때
            user = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );
        }

        Page<Recipe> recipeList = recipeRepository.findAllByUser(pageable, user);

        return recipeList.map((recipe -> new GetRecipeListResponseDto(recipe, userDetails)));
    }

    @Override
    public Page<GetBoardListResponseDto> getBoardListByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails, String nickname) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        User user;

        // 조회화는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            user = userDetails.getUser();
        } else { // 다른 회원일 때
            user = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );
        }

        Page<Board> boardList = boardRepository.findAllByUser(pageable, user);

        return boardList.map((board -> new GetBoardListResponseDto(board, userDetails)));
    }

    // TODO: N+1 문제 해결
    @Override
    public Page<GetRecipeListResponseDto> getLikedRecipeListByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails, String nickname) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        User user;

        // 조회화는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            user = userDetails.getUser();
        } else { // 다른 회원일 때
            user = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );
        }

        Page<Recipe> likedRecipeList = recipeRepository.findAllByRecipeLikesList(user.getId(), pageable);

//        likedRecipeList.forEach(System.out::println);

        return likedRecipeList.map(recipe -> new GetRecipeListResponseDto(recipe, userDetails));
    }

    @Override
    public Page<GetBoardListResponseDto> getLikedBoardListByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails, String nickname) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        User user;

        // 조회화는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            user = userDetails.getUser();
        } else { // 다른 회원일 때
            user = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );
        }

        Page<Board> likedBoardList = boardRepository.findAllByBoardLikesList(user.getId(), pageable);

        return likedBoardList.map(board -> new GetBoardListResponseDto(board, userDetails));
    }

    // 팔로잉 목록
    public Page<GetFollowingListResponseDto> getFollowingListByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails, String nickname) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        User user;

        // 조회화는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            user = userDetails.getUser();
        } else { // 다른 회원일 때
            user = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );
        }

        Page<Follow> followingList = followRepository.findAllByFromUser(pageable, user);



        return followingList.map(GetFollowingListResponseDto::new);
    }

    @Override
    public Page<GetFollowerListResponseDto> getFollowerListByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails, String nickname) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        User user;

        // 조회화는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            user = userDetails.getUser();
        } else { // 다른 회원일 때
            user = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );
        }

        Page<Follow> followerList = followRepository.findAllByToUser(pageable, user);

        return followerList.map(GetFollowerListResponseDto::new);
    }
}
