package com.sparta.backend.domain.Recipe;

import com.sparta.backend.domain.User;
import com.sparta.backend.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeCommentTest {
    @Nested
    @DisplayName("레시피 댓글 객체 생성")
    class CreateRecipeComment{
        private String content;
        private User recipeOwner;
        private User commentWriter;
        private Recipe recipe;

        @BeforeEach
        void setup(){
            content = "normal content";
            recipeOwner = new User(
                    1L,
                    "abc@gmail.com",
                    "mock password",
                    "recipeOwener",
                    null,
                    UserRole.USER,
                    "Y"
            );
            commentWriter = new User(
                    1L,
                    "comment@gmail.com",
                    "mock password",
                    "commentWriter",
                    null,
                    UserRole.USER,
                    "Y"
            );
            recipe = new Recipe(
                    1L,
                    "test recipe title",
                    "test recipe content",
                    5000,
                    recipeOwner
            );
        }

        @Test
        @DisplayName("정상 케이스")
        void createComment_Normal(){
            //given, when
            RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
            //then
            assertEquals(content,comment.getContent());
            assertEquals(commentWriter, comment.getUser());
            assertEquals(recipe, comment.getRecipe());
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCases{
            @Nested
            @DisplayName("권한 관련")
            class userFail{

                @Test
                @DisplayName("commentWriter = null")
                void userNull(){
                    //given
                    commentWriter = null;

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
                        RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
                    });
                    //then
                    assertEquals("로그인 되지 않은 사용자입니다", exception.getMessage());
                }

                @Test
                @DisplayName("commentWriter의 id < 0")
                void userIdMinus(){
                    //given
                    commentWriter = new User(
                            -20L,
                            "comment@gmail.com",
                            "mock password",
                            "commentWriter",
                            null,
                            UserRole.USER,
                            "Y"
                    );

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
                        RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
                    });
                    //then
                    assertEquals("회원 id가 유효하지 않습니다.", exception.getMessage());
                }

                @Test
                @DisplayName("commentWriter의 id == null")
                void userIdNull(){
                    //given
                    commentWriter = new User(
                            null,
                            "comment@gmail.com",
                            "mock password",
                            "commentWriter",
                            null,
                            UserRole.USER,
                            "Y"
                    );

                    //when
                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
                        RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
                    });
                    //then
                    assertEquals("회원 id가 유효하지 않습니다.", exception.getMessage());
                }
            }
        }


    }
}