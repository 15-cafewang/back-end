package com.sparta.backend.service;

import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.Recipe;
import com.sparta.backend.domain.Tag;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.RecipesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipesService {

    private final RecipesRepository recipesRepository;
    private final S3Uploader s3Uploader;

    //todo: user정보도 넣어줘야 함
    public Recipe saveRecipe(PostRecipeRequestDto requestDto) throws IOException {
        String saveImage = s3Uploader.upload(requestDto.getImage(),"recipeImage");
        Recipe recipe = new Recipe(requestDto.getTitle(),requestDto.getContent(),saveImage);
        return recipesRepository.save(recipe);
    }

    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipesRepository.findById(recipeId).orElseThrow(()->
                new CustomErrorException("해당 아이디가 존재하지 않습니다")
        );
        recipesRepository.deleteById(recipeId);
        //todo:S3서버의 이미지도 지우기
    }

    @Transactional
    public Recipe updateRecipe(Long recipeId,PostRecipeRequestDto requestDto, UserDetailsImpl userDetails) {
        //게시글 존재여부확인
        Recipe recipe = recipesRepository.findById(recipeId).orElseThrow(()->new CustomErrorException("해당 게시물을 찾을 수 없습니다"));

        String imageUrl = recipe.getImage();//사진은 일단 기존 포스트의 URL(사진은 업데이트 안 했을 경우 대비)
//        User user = userDetails.getUser();
        String title = requestDto.getTitle();
        String content = requestDto.getContent();

        //todo: 게시글에 저장되어있는 사용자의 username과 현재 사용자의 username 비교하기
        //todo: S3에 있는 사진 삭제하고 다시 업로드

        //게시글 업데이트
        return recipe.updateRecipe(title, content, "this is mock Image URL");
    }
}
