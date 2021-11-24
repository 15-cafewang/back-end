package com.sparta.backend.mvc;

import com.sparta.backend.controller.recipe.RecipeController;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.domain.user.UserRole;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.security.WebSecurityConfig;
import com.sparta.backend.service.recipe.RecipeService;
import com.sparta.backend.service.recipe.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.FileInputStream;
import java.security.Principal;

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

    public void mockUserSetup() {
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

    @Nested
    @DisplayName("정상 케이스")
    class SuccessCases {
//        @Test
//        @DisplayName("일반적인 레시피 등록")
//        void saveRecipe() throws Exception {
//            mockUserSetup();
//            MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
//            MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
//            mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
//                            .file(image)
//                            .file(image2)
//                            .param("title", "this is title")
//                            .param("content", "this is content")
//                            .principal(mockPrincipal))
//                    .andExpect(status().isOk());
//        }
//
//        @Test
//        @DisplayName("사진 미등록")
//        void noImage() throws Exception {
//            mockUserSetup();
//            MockMultipartFile image = new MockMultipartFile("image", null, "image/jpg",InputStream.nullInputStream());
////            MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
//            mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
//                            .file(image)
////                            .file(image2)
//                            .param("title", "this is title")
//                            .param("content", "this is content")
//                            .principal(mockPrincipal))
//                    .andExpect(status().isOk());
//        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {

        @Nested
        @DisplayName("title입력관련")
        class titleRelated{
            @Test
            @DisplayName("null 제목")
            void titleNUll() throws Exception {
                mockUserSetup();
                MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                .file(image)
                                .file(image2)
                                .param("content", "this is content")
                                .principal(mockPrincipal))
                        .andExpect(status().is4xxClientError())
                        .andExpect(result -> assertEquals("제목은 필수입니다.", result.getResolvedException().getMessage()));
            }

            @Test
            @DisplayName("빈 제목")
            void titleEmpty() throws Exception {
                mockUserSetup();
                MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                .file(image)
                                .file(image2)
                                .param("title","")
                                .param("content", "this is content")
                                .principal(mockPrincipal))
                        .andExpect(status().is4xxClientError())
                        .andExpect(result -> assertEquals("제목은 필수입니다.", result.getResolvedException().getMessage()));
            }

            @Test
            @DisplayName("공간만 있는 빈 제목")
            void titleTrimEmpty() throws Exception {
                mockUserSetup();
                MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                .file(image)
                                .file(image2)
                                .param("title","    ")
                                .param("content", "this is content")
                                .principal(mockPrincipal))
                        .andExpect(status().is4xxClientError())
                        .andExpect(result -> assertEquals("제목은 필수입니다.", result.getResolvedException().getMessage()));
            }

            @Test
            @DisplayName("너무 긴 제목")
            void titleTooLong() throws Exception {
                mockUserSetup();
                MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                .file(image)
                                .file(image2)
                                .param("title", "If you're visiting this page, you're likely here because you're searching for a random sentence. Sometimes a random word just isn't enough, and that is where the random sentence generator comes into play. By inputting the desired number, you can make a list of as many random sentences as you want or need. Producing random sentences can be helpful in a number of different ways.\n" +
                                        "\n" +
                                        "For writers, a random sentence can help them get their creative juices flowing. Since the topic of the sentence is completely unknown, it forces the writer to be creative when the sentence appears. There are a number of different ways a writer can use the random sentence for creativity. The most common way to use the sentence is to begin a story. Another option is to include it somewhere in the story. A much more difficult challenge is to use it to end a story. In any of these cases, it forces the writer to think creatively since they have no idea what sentence will appear from the tool.\n" +
                                        "\n" +
                                        "For those writers who have writers' block, this can be an excellent way to take a step to crumbling those walls. By taking the writer away from the subject matter that is causing the block, a random sentence may allow them to see the project they're working on in a different light and perspective. Sometimes all it takes is to get that first sentence down to help break the block.\n" +
                                        "\n" +
                                        "It can also be successfully used as a daily exercise to get writers to begin writing. Being shown a random sentence and using it to complete a paragraph each day can be an excellent way to begin any writing session.\n" +
                                        "\n" +
                                        "Random sentences can also spur creativity in other types of projects being done. If you are trying to come up with a new concept, a new idea or a new product, a random sentence may help you find unique qualities you may not have considered. Trying to incorporate the sentence into your project can help you look at it in different and unexpected ways than you would normally on your own.\n" +
                                        "\n" +
                                        "It can also be a fun way to surprise others. You might choose to share a random sentence on social media just to see what type of reaction it garners from others. It's an unexpected move that might create more conversation than a typical post or tweet.\n" +
                                        "\n")
                                .param("content", "this is content")
                                .principal(mockPrincipal))
                        .andExpect(status().is4xxClientError())
                        .andExpect(result -> assertEquals("제목이 너무 깁니다.", result.getResolvedException().getMessage()));
            }

            @Nested
            @DisplayName("이미지 관련")
            class ImageRelated {
                @Test
                @DisplayName("이미지 6장 초과")
                void imageTooMany() throws Exception {
                    mockUserSetup();
                    MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    MockMultipartFile image2 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    MockMultipartFile image3 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    MockMultipartFile image4 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    MockMultipartFile image5 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    MockMultipartFile image6 = new MockMultipartFile("image", "imagefile2.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                    .file(image).file(image2).file(image3).file(image4).file(image5).file(image6)
                                    .param("title","this is title")
                                    .param("content", "this is content")
                                    .principal(mockPrincipal))
                            .andExpect(status().is4xxClientError())
                            .andExpect(result -> assertEquals("사진은 5장을 초과할 수 없습니다.", result.getResolvedException().getMessage()));
                }
            }

            @Nested
            @DisplayName("태그 관련")
            class TagRelated {
                @Test
                @DisplayName("너무 많은 태그")
                void tagTooMany() throws Exception {
                    mockUserSetup();
                    MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                    .file(image)
                                    .param("title","this is title")
                                    .param("content", "this is content")
                                    .param("tag","aa").param("tag","dd").param("tag","gg").param("tag","jj").param("tag","mm")
                                    .param("tag","bb").param("tag","ee").param("tag","hh").param("tag","kk").param("tag","nn")
                                    .param("tag","cc").param("tag","ff").param("tag","ii").param("tag","ll").param("tag","oo")
                                    .principal(mockPrincipal))
                            .andExpect(status().is4xxClientError())
                            .andExpect(result -> assertEquals("태그가 너무 많습니다.", result.getResolvedException().getMessage()));
                }

                @Test
                @DisplayName("너무 긴 태그")
                void tagTooLong() throws Exception {
                    mockUserSetup();
                    MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                    .file(image)
                                    .param("title","this is title")
                                    .param("content", "this is content")
                                    .param("tag","아침에 눈을 뜨면 네 생각이나\n" +
                                            "창밖을 바라보다 네 생각이나\n" +
                                            "그렇게 멍하니 또 하루가 흘러가\n" +
                                            "너도 날 가끔씩은 떠 올릴까 네 생각이나\n" +
                                            "어느새 내 주변의 모든 건\n" +
                                            "익숙한 향기로 너에게 물들어\n" +
                                            "화초에 꽃이 피어 네 생각이나\n" +
                                            "예쁜 걸 볼 때마다 네 생각이나\n" +
                                            "내 취향은 아니지만 네가 좋아하는\n" +
                                            "그 노랫말 하루 종일 흥얼거려 네 생각이나\n" +
                                            "사랑이 내게도 찾아왔나 봐\n" +
                                            "어느새 내 주변의 모든 건\n" +
                                            "처음 보는 색으로 내 맘처럼 피어나")
                                    .principal(mockPrincipal))
                            .andExpect(status().is4xxClientError())
                            .andExpect(result -> assertEquals("태그가 너무 깁니다.", result.getResolvedException().getMessage()));
                }
            }

        }


    }


//    @Test
//    @DisplayName("레시피등록-validator")
//    void validTest(){
//        //given
//        String title = "제목입니다";
//        String content = "내용입니다";
//        Integer location = 5000;
//        List<String> tag = Arrays.asList("tagA","tagB");
//        MultipartFile[] image = null;
//        PostRecipeRequestDto requestDto = new PostRecipeRequestDto(title,content,location,tag, image);
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
