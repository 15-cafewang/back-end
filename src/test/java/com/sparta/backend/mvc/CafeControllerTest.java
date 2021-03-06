package com.sparta.backend.mvc;

import com.sparta.backend.controller.cafe.CafeController;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.domain.user.UserRole;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.security.WebSecurityConfig;
import com.sparta.backend.service.cafe.CafeService;
import com.sparta.backend.service.cafe.TagService;
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
        controllers = {CafeController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
public class CafeControllerTest {
    private MockMvc mockMvc;
    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    CafeService cafeService;

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
// Mock ????????? ?????? ??????
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
    @DisplayName("?????? ?????????")
    class SuccessCases {
//        @Test
//        @DisplayName("???????????? ?????? ??????")
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
//        @DisplayName("?????? ?????????")
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
    @DisplayName("?????? ?????????")
    class FailCases {

        @Nested
        @DisplayName("title????????????")
        class titleRelated{
            @Test
            @DisplayName("null ??????")
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
                        .andExpect(result -> assertEquals("????????? ???????????????.", result.getResolvedException().getMessage()));
            }

            @Test
            @DisplayName("??? ??????")
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
                        .andExpect(result -> assertEquals("????????? ???????????????.", result.getResolvedException().getMessage()));
            }

            @Test
            @DisplayName("????????? ?????? ??? ??????")
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
                        .andExpect(result -> assertEquals("????????? ???????????????.", result.getResolvedException().getMessage()));
            }

            @Test
            @DisplayName("?????? ??? ??????")
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
                        .andExpect(result -> assertEquals("????????? ?????? ?????????.", result.getResolvedException().getMessage()));
            }

            @Nested
            @DisplayName("????????? ??????")
            class ImageRelated {
                @Test
                @DisplayName("????????? 6??? ??????")
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
                            .andExpect(result -> assertEquals("????????? 5?????? ????????? ??? ????????????.", result.getResolvedException().getMessage()));
                }
            }

            @Nested
            @DisplayName("?????? ??????")
            class TagRelated {
                @Test
                @DisplayName("?????? ?????? ??????")
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
                            .andExpect(result -> assertEquals("????????? ?????? ????????????.", result.getResolvedException().getMessage()));
                }

                @Test
                @DisplayName("?????? ??? ??????")
                void tagTooLong() throws Exception {
                    mockUserSetup();
                    MockMultipartFile image = new MockMultipartFile("image", "imagefile.jpeg", "image/jpg", new FileInputStream("src/test/java/com/sparta/backend/images/puppy1.jpg"));
                    mockMvc.perform(MockMvcRequestBuilders.multipart("/recipes")
                                    .file(image)
                                    .param("title","this is title")
                                    .param("content", "this is content")
                                    .param("tag","????????? ?????? ?????? ??? ????????????\n" +
                                            "????????? ???????????? ??? ????????????\n" +
                                            "????????? ????????? ??? ????????? ?????????\n" +
                                            "?????? ??? ???????????? ??? ????????? ??? ????????????\n" +
                                            "????????? ??? ????????? ?????? ???\n" +
                                            "????????? ????????? ????????? ?????????\n" +
                                            "????????? ?????? ?????? ??? ????????????\n" +
                                            "?????? ??? ??? ????????? ??? ????????????\n" +
                                            "??? ????????? ???????????? ?????? ????????????\n" +
                                            "??? ????????? ?????? ?????? ???????????? ??? ????????????\n" +
                                            "????????? ????????? ???????????? ???\n" +
                                            "????????? ??? ????????? ?????? ???\n" +
                                            "?????? ?????? ????????? ??? ????????? ?????????")
                                    .principal(mockPrincipal))
                            .andExpect(status().is4xxClientError())
                            .andExpect(result -> assertEquals("????????? ?????? ?????????.", result.getResolvedException().getMessage()));
                }
            }

        }


    }


//    @Test
//    @DisplayName("????????????-validator")
//    void validTest(){
//        //given
//        String title = "???????????????";
//        String content = "???????????????";
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
//        Assertions.assertThat(messages).contains("????????? ?????? ?????? ????????????.","?????? ????????? ?????? ??????");
//        assertEquals("????????? ???????????? ???????????????.",  exception.getMessage());
//    }
}
