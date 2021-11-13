package com.sparta.backend.service;

import com.sparta.backend.domain.Board;
import com.sparta.backend.domain.Follow;
import com.sparta.backend.domain.recipe.Recipe;
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
    private final RecipeLikesRepository recipeLikesRepository;
    private final BoardRepository boardRepository;
    private final BoardLikesRepository boardLikesRepository;

    // 마이페이지
    @Override
    public GetUserinfoResponseDto getUserInfo(UserDetailsImpl userDetails, String nickname) {

        String image;
        int followCount;
        int followingCount;
        boolean followStatus = false;

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
                followStatus = true;
            }
        }

        return new GetUserinfoResponseDto(image, nickname, followCount, followingCount, followStatus);
    }

    // 내가 쓴 레시피 목록 조회
    @Override
    public Page<GetRecipeListResponseDto> getRecipeListByPage(int page,
                                                              int size,
                                                              boolean isAsc,
                                                              String sortBy,
                                                              String nickname,
                                                              UserDetailsImpl userDetails) {

        Pageable pageable = getPageable(page, size, isAsc, sortBy);

        User user = getUser(userDetails, nickname);

        Page<Recipe> recipeList = recipeRepository.findAllByUser(pageable, user);

        return recipeList.map((recipe -> new GetRecipeListResponseDto(recipe, userDetails, recipeLikesRepository)));
    }

    // 내가 쓴 게시글 목록 조회
    @Override
    public Page<GetBoardListResponseDto> getBoardListByPage(int page,
                                                            int size,
                                                            boolean isAsc,
                                                            String sortBy,
                                                            String nickname,
                                                            UserDetailsImpl userDetails) {

        Pageable pageable = getPageable(page, size, isAsc, sortBy);

        User user = getUser(userDetails, nickname);

        Page<Board> boardList = boardRepository.findAllByUser(pageable, user);

        return boardList.map((board -> new GetBoardListResponseDto(board, userDetails, boardLikesRepository)));
    }

    // TODO: N+1 문제 해결
    // 내가 좋아요한 레시피 목록 조회
    @Override
    public Page<GetRecipeListResponseDto> getLikedRecipeListByPage(int page,
                                                                   int size,
                                                                   boolean isAsc,
                                                                   String sortBy,
                                                                   String nickname,
                                                                   UserDetailsImpl userDetails) {

        Pageable pageable = getPageable(page, size, isAsc, sortBy);

        User user = getUser(userDetails, nickname);

        Page<Recipe> likedRecipeList = recipeRepository.findAllByRecipeLikesList(user.getId(), pageable);

        return likedRecipeList.map(recipe -> new GetRecipeListResponseDto(recipe, userDetails, recipeLikesRepository));
    }

    // 내가 좋아요한 게시글 목록 조회
    @Override
    public Page<GetBoardListResponseDto> getLikedBoardListByPage(int page,
                                                                 int size,
                                                                 boolean isAsc,
                                                                 String sortBy,
                                                                 String nickname,
                                                                 UserDetailsImpl userDetails) {

        Pageable pageable = getPageable(page, size, isAsc, sortBy);

        User user = getUser(userDetails, nickname);

        Page<Board> likedBoardList = boardRepository.findAllByBoardLikesList(user.getId(), pageable);

        return likedBoardList.map(board -> new GetBoardListResponseDto(board, userDetails, boardLikesRepository));
    }

    // 팔로잉 목록 조회
    @Override
    public Page<GetFollowingListResponseDto> getFollowingListByPage(int page,
                                                                    int size,
                                                                    boolean isAsc,
                                                                    String sortBy,
                                                                    String nickname,
                                                                    UserDetailsImpl userDetails) {

        Pageable pageable = getPageable(page, size, isAsc, sortBy);

        User user = getUser(userDetails, nickname);

        Page<Follow> followingList = followRepository.findAllByFromUser(pageable, user);

        return followingList.map(GetFollowingListResponseDto::new);
    }

    // 팔로워 목록 조회
    @Override
    public Page<GetFollowerListResponseDto> getFollowerListByPage(int page,
                                                                  int size,
                                                                  boolean isAsc,
                                                                  String sortBy,
                                                                  String nickname,
                                                                  UserDetailsImpl userDetails) {

        Pageable pageable = getPageable(page, size, isAsc, sortBy);

        User user = getUser(userDetails, nickname);

        Page<Follow> followerList = followRepository.findAllByToUser(pageable, user);

        return followerList.map(GetFollowerListResponseDto::new);
    }

    private Pageable getPageable(int page, int size, boolean isAsc, String sortBy) {

        page -= 1;

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(page, size, sort);
    }

    private User getUser(UserDetailsImpl userDetails, String nickname) {

        User user;

        // 조회하는 회원이 로그인한 회원일 때
        if (nickname.equals(userDetails.getUser().getNickname())) {
            user = userDetails.getUser();
        } else { // 다른 회원일 때
            user = userRepository.findByNickname(nickname).orElseThrow(
                    () -> new NullPointerException("존재하지 않는 회원입니다")
            );
        }

        return user;
    }
}
