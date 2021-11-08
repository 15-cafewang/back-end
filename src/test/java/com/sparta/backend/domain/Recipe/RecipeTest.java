package com.sparta.backend.domain.Recipe;

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
    class CreateRecipe{

        private String title;
        private String content;
        private int price;
        private User user;

        @BeforeEach
        void setup(){
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

        @Test
        @DisplayName("정상 케이스")
        void createRecipe_Normal(){
            //given, when
            Recipe recipe = new Recipe(title, content,price,user);

            //then
            assertEquals(title,recipe.getTitle());
            assertEquals(content, recipe.getContent());
            assertEquals(price, recipe.getPrice());
            assertEquals(user, recipe.getUser());
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases{
            @Nested
            @DisplayName("권한 관련")
            class userFail{
                @Test
                @DisplayName("null")
                void userNull(){
                    //given
                    user = null;

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
                        Recipe recipe = new Recipe(title, content,price,user);
                    });
                    //then
                    assertEquals("로그인 되지 않은 사용자입니다", exception.getMessage());
                }
            }
        }
    }

}

























