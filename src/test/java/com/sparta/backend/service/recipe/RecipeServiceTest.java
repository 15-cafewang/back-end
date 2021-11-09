package com.sparta.backend.service.recipe;

import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.repository.RecipeImageRepository;
import com.sparta.backend.repository.RecipeLikesRepository;
import com.sparta.backend.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    //레시피 저장 테스트
        //권한관련
             //로그인 한사람이 저장시도- 성공 -> 모델 테스트에서..
            //로그인 안 한사람이 저장시도- 실패 -> 모델 테스트에서..
        //내용 유무관련
            //일반적인 레시피 저장
                // 저장테스트(저장 되는지)
                //->상세조회 테스트(잘 나오는지)
            //아무 내용 없는 레시피 - 실패
            //제목만 없는 레시피 - 실패
            //내용만 없는 레시피 - 실패
            //사진만 없는 레시피 - 성공
            //가격만 없는 레시피 - 성공
            //사진 5장 다 올린 레시피 - 성공
            //사진 5장 초과(6장,7장) 올린 레시피 - 실패
    @Mock
    RecipeRepository recipeRepository;
    @Mock
    S3Uploader s3Uploader;

    @Test
    @DisplayName("이미지 S3업로드")
    void uploadManyImagesToS3_Normal(@Mock RecipeImageRepository recipeImageRepository,
                                     @Mock RecipeLikesRepository recipeLikesRepository,
                                     @Mock AmazonS3Client amazonS3Client,
                                     @Mock S3Uploader s3Uploader,
                                     @Mock RecipeRepository recipeRepository) throws IOException {

        RecipeService recipeService = new RecipeService(recipeRepository, recipeLikesRepository,amazonS3Client, s3Uploader, recipeImageRepository);
        //given
        List<String> tags = Arrays.asList("tagA","tagB","tagC");
        MockMultipartFile image1 = new MockMultipartFile("image1","image1","image/png",getClass().getResourceAsStream("/images/puppy1.jpg"));
        PostRecipeRequestDto requestDto = new PostRecipeRequestDto(
                "test title",
                "test content",
                5000,
                tags,
                image1,null,null,null,null
        );
        //s3Uploader.upload 스터빙
        when(s3Uploader.upload(requestDto.getImage1(),"myDirectory")).thenReturn("s3imageUrl");

//        when(recipeImageRepository.saveAll())
        //when

        //then
    }


}