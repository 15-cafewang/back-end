package com.sparta.backend.mvc;

import com.sparta.backend.controller.RecipeController;
import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.security.WebSecurityConfig;
import com.sparta.backend.service.recipe.RecipeService;
import com.sparta.backend.service.recipe.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.Arrays;

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
        User testUser = new User(email, password,nickname, image, role, "Y");
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("레시피 등록")
    void saveRecipe() throws Exception {
        this.mockUserSetup();
        InputStream is = this.getClass().getResourceAsStream("/images/puppyy1.jpg");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image","test.jpg","text/plain","hihi".getBytes());
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/recipes");
        builder.with(request -> {request.setMethod("POST"); return request;});

        mockMvc.perform(builder.file(mockMultipartFile)
                        .param("title","this is title")
                        .param("content","this is content")
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk());

    }
}
