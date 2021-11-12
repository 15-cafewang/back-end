package com.sparta.backend.integration;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.recipe.RecipeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecipeIntegrationTest {
    @Autowired
    RecipeService recipeService;

    User testUser;

    public void mockUserSetup() {
// Mock 테스트 유져 생성
        String nickname = "testUser";
        String password = "hope!@#";
        String email = "hope@sparta.com";
        UserRole role = UserRole.USER;
        String image = "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
        testUser = new User(email, password, nickname, image, role, "Y");
//        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
//        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @Order(1)
    @DisplayName("회원가입 없이 레시피 저장하면 에러발생")
    void test1() throws IOException {
        //given
        mockUserSetup();

        String title = "이것이 레시피다";
        String content = "내용입니다. 맛있다 냠냠냠";
        Integer price = 5000;
        List<String> tag = Arrays.asList("가가,나나,다다");
        MockMultipartFile image1 = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
        MockMultipartFile[] image = {image1};

        PostRecipeRequestDto requestDto = new PostRecipeRequestDto(
                title, content, price,tag, image
        );
        User notValidUser = null;

        //when
        Exception exception = assertThrows(IllegalArgumentException.class,()->{
            Recipe recipe = recipeService.saveRecipe(requestDto,notValidUser);
        });

        //then
        assertEquals("로그인 되지 않은 사용자입니다", exception.getMessage());
    }

}
