package com.sparta.backend.service.Recipe;

import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.repository.RecipeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    //레시피 저장 테스트
        //권환관련
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
//    @Nested
//    @DisplayName("레시피 저장")
//    class SaveRecipe{
//
//        @Mock
//        RecipeRepository recipeRepository;
//        @Mock
//        RecipeService recipeService;
//
//        @Nested
//        @DisplayName("칼럼 유무 별 상세조회 테스트")
//        class Auth{
//            @Test
//            @DisplayName("일반적인 레시피")
//            void saveRecularRecipe() throws IOException {
//                //저장-> 조회 되는지?
//                //given
//                User user = new User(
//                        "abc@gmail.com",
//                        "mock password",
//                        "abc",
//                        null,
//                        UserRole.USER,
//                        "Y");
//                Recipe recipe = new Recipe(
//                        1L,
//                        "mock title",
//                        "mock content",
//                        5000,
//                        user
//                );
//                List<String> tagList = new ArrayList<>();
//                tagList.add("tag-a");
//                tagList.add("tag-b");
//                tagList.add("tag-c");
//
//                PostRecipeRequestDto requestDto = new PostRecipeRequestDto(
//                        "mock title",
//                        "mock content",
//                        5000,
//                        tagList, null, null, null, null, null
//                );
//
//                when(recipeService.findById(1L)).thenReturn(Optional.of(recipe));
//                when(recipeRepository.save(recipe)).thenReturn(recipe);
//
//                //when
//                Recipe savedRecipe = recipeService.saveRecipe(requestDto, user);
//                //then
//                assertEquals(recipe,user.getRecipeList().get(0));
//            }
//        }
//    }

}