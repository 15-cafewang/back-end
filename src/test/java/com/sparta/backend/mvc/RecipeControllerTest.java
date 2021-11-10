package com.sparta.backend.mvc;

import com.sparta.backend.controller.RecipeController;
import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.security.WebSecurityConfig;
import com.sparta.backend.service.recipe.RecipeService;
import com.sparta.backend.service.recipe.TagService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(
        controllers = {RecipeController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
public class RecipeControllerTest {
    private MockMvc mockMvc;
    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    RecipeService recipeService;

    @MockBean
    TagService tagService;

    @Autowired
    private Validator validatorInjected;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
// Mock 테스트 유져 생성
        String nickname = "testUser";
        String password = "hope!@#";
        String email = "hope@sparta.com";
        UserRole role = UserRole.USER;
        String image = "https://user-images.githubusercontent.com/76515226/140890775-30641b72-226a-4068-8a0a-9a306e8c68b4.png";
        User testUser = new User(email, password, nickname, image, role, "Y");
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    //todo:
    @Test
    @DisplayName("레시피 등록")
    void saveRecipe() throws Exception {
        this.mockUserSetup();
        MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
        MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                        .file(image)
                        .file(image2)
//                        .param("title", "this is title")
                        .param("content", "this is content")
                        .principal(mockPrincipal))
//                .andExpect(status().isOk());
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertEquals("제목은 필수입니다.",result.getResolvedException().getMessage()));

    }

//    @Test
//    @DisplayName("레시피등록-validator")
//    void validTest(){
//        //given
//        String title = "제목입니다";
//        String content = "내용입니다";
//        Integer price = 5000;
//        List<String> tag = Arrays.asList("tagA","tagB");
//        MultipartFile[] image = null;
//        PostRecipeRequestDto requestDto = new PostRecipeRequestDto(title,content,price,tag, image);
//
//        //when
//        Set<ConstraintViolation<PostRecipeRequestDto>> validate = validatorInjected.validate(requestDto);
//
        // then
//        Iterator<ConstraintViolation<PostRecipeRequestDto>> iterator = validate.iterator();
//        List<String> messages = new ArrayList<>();
//        while (iterator.hasNext()) {
//            ConstraintViolation<PostRecipeRequestDto> next = iterator.next();
//            messages.add(next.getMessage());
//            System.out.println("message = " + next.getMessage());
//        }
//
//        Assertions.assertThat(messages).contains("제목은 필수 입력 값입니다.","레시피 형식에 맞지 않음");
//        assertEquals("제목이 입력되지 않았습니다.",  exception.getMessage());
//    }
}
