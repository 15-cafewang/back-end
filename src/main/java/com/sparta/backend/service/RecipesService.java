package com.sparta.backend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.Recipe;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.recipes.RecipeCommentResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeDetailResponsetDto;
import com.sparta.backend.dto.response.recipes.RecipeListResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.RecipesRepository;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipesService {

    private final RecipesRepository recipesRepository;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";

    //레시피 저장
    //todo: user정보도 넣어줘야 함
    public Recipe saveRecipe(PostRecipeRequestDto requestDto) throws IOException {
        String saveImage = s3Uploader.upload(requestDto.getImage(),"recipeImage");
        Recipe recipe = new Recipe(requestDto.getTitle(),requestDto.getContent(),requestDto.getPrice(),saveImage);
        return recipesRepository.save(recipe);
    }

    //레시피 삭제
    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipesRepository.findById(recipeId).orElseThrow(()->
                new CustomErrorException("해당 아이디가 존재하지 않습니다")
        );
        recipesRepository.deleteById(recipeId);
        //todo:S3서버의 이미지도 지우기
        deleteS3(recipe.getImage());
    }

    //레시피 수정
    @Transactional
    public Recipe updateRecipe(Long recipeId,PostRecipeRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        //게시글 존재여부확인
        Recipe recipe = recipesRepository.findById(recipeId).orElseThrow(()->new CustomErrorException("해당 게시물을 찾을 수 없습니다"));

        String imageUrl = recipe.getImage();//사진은 일단 기존 포스트의 URL(사진은 업데이트 안 했을 경우 대비)
//        User user = userDetails.getUser();
        String title = requestDto.getTitle();
        int price = requestDto.getPrice();
        String content = requestDto.getContent();

        //todo: 게시글에 저장되어있는 사용자의 nickname과 현재 사용자의 nickname 비교하기

        //S3에 있는 사진 삭제하고 다시 업로드
        if(requestDto.getImage() != null){
            Recipe foundRecipe = recipesRepository.findById(recipeId).orElseThrow(()->
                    new CustomErrorException("해당 게시물을 찾을 수 없습니다"));
            deleteS3(foundRecipe.getImage());
            imageUrl = s3Uploader.upload(requestDto.getImage(),"recipeImage");
            if(imageUrl == null) throw new CustomErrorException("이미지 업르드에 실패하였습니다");
        }

        //게시글 업데이트 todo: user생기면 user정보도 넣어줘야 한다.
        return recipe.updateRecipe(title, content, price,imageUrl);
    }

    //S3 이미지 삭제
    public void deleteS3(@RequestParam String imageName){
        //https://S3 버킷 URL/버킷에 생성한 폴더명/이미지이름
        String keyName = imageName.split("/")[4]; // 이미지이름만 추출

        try {amazonS3Client.deleteObject(bucket + "/recipeImage", keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }

    //레시피 상세조회
    public RecipeDetailResponsetDto getRecipeDetail(Long recipeId, UserDetailsImpl userDetails) {
        Recipe recipe = recipesRepository.findById(recipeId).orElseThrow(()->
                new CustomErrorException("해당 게시물이 존재하지 않습니다"));
        Long foudnRecipeId = recipe.getId();
        //todo:nickname가져오기
//        String nickname = recipe.getUser().getNickname();
        String title = recipe.getTitle();
        String content = recipe.getContent();
        LocalDateTime regDate = recipe.getRegDate();
        int likeCount = recipe.getRecipeLikesList().size();
        //todo:likeStatus 좋아요 기능 추가 후에 작업해야 함.

        String image = recipe.getImage();
        List<String> tagNames = new ArrayList<>();
        recipe.getTagList().stream().map((tag)->tagNames.add(tag.getName()));

        RecipeDetailResponsetDto responsetDto = new RecipeDetailResponsetDto(
                recipeId, "mock nickname", title, content, regDate, likeCount, true, image, tagNames);

        return responsetDto;
    }

    public Page<RecipeListResponseDto> getRecipesByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Recipe> recipes = recipesRepository.findAll(pageable);

        Page<RecipeListResponseDto> responseDtos = recipes.map(RecipeListResponseDto::new);

        return responseDtos;
    }
}
