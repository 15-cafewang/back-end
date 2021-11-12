package com.sparta.backend.integration;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.request.user.SignupRequestDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.UserService;
import com.sparta.backend.service.recipe.RecipeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    User user = null;
    Recipe createdRecipe = null;

    @Test
    @Order(1)
    @DisplayName("회원가입 없이 레시피 저장하면 에러발생")
    void test1() throws IOException {
        //given

        String title = "이것이 레시피다";
        String content = "내용입니다. 맛있다 냠냠냠";
        Integer price = 5000;
        List<String> tag = Arrays.asList("가가,나나,다다");
        MockMultipartFile image1 = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
        MockMultipartFile[] image = {image1};

        PostRecipeRequestDto requestDto = new PostRecipeRequestDto(
                title, content, price,tag, image
        );

        //when
        Exception exception = assertThrows(IllegalArgumentException.class,()->{
            Recipe recipe = recipeService.saveRecipe(requestDto,user);
        });

        //then
        assertEquals("로그인 되지 않은 사용자입니다", exception.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("회원가입")
    void test2() throws IOException {
        //given
        String email = "hope@sparta.com";
        String password= "1234qwer!@";
        String passwordCheck = "1234qwer!@";
        String nickname = "alex";

        SignupRequestDto requestDto = new SignupRequestDto(email,password,passwordCheck,nickname);

        //when
        user = userService.registerUser(requestDto);

        //then
        assertNotNull(user.getId());
        assertEquals(nickname, user.getNickname());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
        assertEquals(email, user.getEmail());
        assertEquals(UserRole.USER, user.getRole());
    }

}
