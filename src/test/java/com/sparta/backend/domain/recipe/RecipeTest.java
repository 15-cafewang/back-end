package com.sparta.backend.domain.recipe;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {

    @Nested
    @DisplayName("레시피 객체 생성")
    class CreateRecipe {

        private String title;
        private String content;
        private Integer price;
        private User user;

        @BeforeEach
        void setup() {
            title = "normal title";
            content = "normal content";
            price = 5000;
            user = new User(
                    1L,
                    "abc@gmail.com",
                    "mock password",
                    "abc",
                    null,
                    UserRole.USER,
                    "Y"
            );
        }

        @Nested
        @DisplayName("정상케이스")
        class SuccessCases{
            @Test
            @DisplayName("일반적인 케이스")
            void createRecipe_Normal() {
                //given, when
                Recipe recipe = new Recipe(title, content, price, user);

                //then
                assertEquals(title, recipe.getTitle());
                assertEquals(content, recipe.getContent());
                assertEquals(price, recipe.getPrice());
                assertEquals(user, recipe.getUser());
            }

            @Test
            @DisplayName("가격 null")
            void priceNull() {
                //given, when
                price = null;
                Recipe recipe = new Recipe(title, content, price, user);

                //then
                assertEquals(title, recipe.getTitle());
                assertEquals(content, recipe.getContent());
                assertEquals(price, recipe.getPrice());
                assertEquals(user, recipe.getUser());
            }

            @Test
            @DisplayName("가격 0")
            void priceZero() {
                //given, when
                price = null;
                Recipe recipe = new Recipe(title, content, price, user);

                //then
                assertEquals(title, recipe.getTitle());
                assertEquals(content, recipe.getContent());
                assertEquals(price, recipe.getPrice());
                assertEquals(user, recipe.getUser());
            }
        }


        @Nested
        @DisplayName("실패 케이스")
        class FailCases {
            @Nested
            @DisplayName("권한 관련")
            class userFail {
                @Test
                @DisplayName("null")
                void userNull() {
                    //given
                    user = null;

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        Recipe recipe = new Recipe(title, content, price, user);
                    });
                    //then
                    assertEquals("로그인 되지 않은 사용자입니다", exception.getMessage());
                }

                //마이너스가 될 일은 없지만, db에서 직접 id를 입력하다 마이너스 입력할 수도 있음.
                @Test
                @DisplayName("id마이너스")
                void userIdMinus() {
                    //given
                    user = new User(
                            -20L,
                            "abc@gmail.com",
                            "mock password",
                            "abc",
                            null,
                            UserRole.USER,
                            "Y"
                    );

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        Recipe recipe = new Recipe(title, content, price, user);
                    });
                    //then
                    assertEquals("회원 id가 유효하지 않습니다.", exception.getMessage());
                }
            }

            @Nested
            @DisplayName("입력값 관련")
            class InputFail {

                @Test
                @DisplayName("제목 null")
                void titleNull() {
                    //given
                    title = null;

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        new Recipe(title, content, price, user);
                    });

                    assertEquals("제목이 입력되지 않았습니다.",  exception.getMessage());
                }

                @Test
                @DisplayName("제목 빈 문자열")
                void titleEmpty() {
                    //given
                    title = "";

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        new Recipe(title, content, price, user);
                    });

                    assertEquals("제목이 입력되지 않았습니다.",  exception.getMessage());
                }

                @Test
                @DisplayName("제목 길이 200자초과")
                void titleTooLong() {
                    //given
                    title = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        new Recipe(title, content, price, user);
                    });

                    assertEquals("제목의 길이가 200자를 초과하였습니다.",  exception.getMessage());
                }

                @Test
                @DisplayName("내용 길이 1000자초과")
                void contentTooLong() {
                    //given
                    content = "If you're visiting this page, you're likely here because you're searching for a random sentence. Sometimes a random word just isn't enough, and that is where the random sentence generator comes into play. By inputting the desired number, you can make a list of as many random sentences as you want or need. Producing random sentences can be helpful in a number of different ways.\n" +
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
                            "\n" +
                            "These are just a few ways that one might use the random sentence generator for their benefit. If you're not sure if it will help in the way you want, the best course of action is to try it and see. Have several random sentences generated and you'll soon be able to see if they can help with your project.\n" +
                            "\n" +
                            "Our goal is to make this tool as useful as possible. For anyone who uses this tool and comes up with a way we can improve it, we'd love to know your thoughts. Please contact us so we can consider adding your ideas to make the random sentence generator the best it can be.";

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        new Recipe(title, content, price, user);
                    });

                    assertEquals("내용의 길이가 1000자를 초과하였습니다",  exception.getMessage());
                }
            }
        }
    }
}

























