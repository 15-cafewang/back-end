package com.sparta.backend.service;

import com.sparta.backend.dto.response.mypage.GetMypageResponseDto;
import com.sparta.backend.repository.BoardRepository;
import com.sparta.backend.repository.FollowRepository;
import com.sparta.backend.repository.RecipeRepository;
import com.sparta.backend.repository.UserRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MypageServiceImpl implements MypageService{

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final BoardRepository boardRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public void mypage() {
        
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
