//package com.sparta.backend.domain.recipe;
//
//import com.sparta.backend.domain.user.User;
//import com.sparta.backend.domain.user.UserRole;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RecipeCommentTest {
//    @Nested
//    @DisplayName("카페 댓글 객체 생성")
//    class CreateRecipeComment{
//        private String content;
//        private User recipeOwner;
//        private User commentWriter;
//        private Recipe recipe;
//
//        @BeforeEach
//        void setup(){
//            content = "normal content";
//            recipeOwner = new User(
//                    1L,
//                    "abc@gmail.com",
//                    "mock password",
//                    "recipeOwener",
//                    null,
//                    UserRole.USER,
//                    "Y"
//            );
//            commentWriter = new User(
//                    1L,
//                    "comment@gmail.com",
//                    "mock password",
//                    "commentWriter",
//                    null,
//                    UserRole.USER,
//                    "Y"
//            );
//            recipe = new Recipe(
//                    1L,
//                    "test recipe title",
//                    "test recipe content",
//                    5000,
//                    recipeOwner
//            );
//        }
//
//        @Test
//        @DisplayName("정상 케이스")
//        void createComment_Normal(){
//            //given, when
//            RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
//            //then
//            assertEquals(content,comment.getContent());
//            assertEquals(commentWriter, comment.getUser());
//            assertEquals(recipe, comment.getRecipe());
//        }
//
//        @Nested
//        @DisplayName("실패 케이스")
//        class FailCases{
//            @Nested
//            @DisplayName("권한 관련")
//            class userFail{
//
//                @Test
//                @DisplayName("commentWriter = null")
//                void userNull(){
//                    //given
//                    commentWriter = null;
//
//                    //when
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
//                    });
//                    //then
//                    assertEquals("로그인 되지 않은 사용자입니다", exception.getMessage());
//                }
//
//                @Test
//                @DisplayName("commentWriter의 id < 0")
//                void userIdMinus(){
//                    //given
//                    commentWriter = new User(
//                            -20L,
//                            "comment@gmail.com",
//                            "mock password",
//                            "commentWriter",
//                            null,
//                            UserRole.USER,
//                            "Y"
//                    );
//
//                    //when
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
//                    });
//                    //then
//                    assertEquals("회원 id가 유효하지 않습니다.", exception.getMessage());
//                }
//
//                @Test
//                @DisplayName("commentWriter의 id == null")
//                void userIdNull(){
//                    //given
//                    commentWriter = new User(
//                            null,
//                            "comment@gmail.com",
//                            "mock password",
//                            "commentWriter",
//                            null,
//                            UserRole.USER,
//                            "Y"
//                    );
//
//                    //when
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content, commentWriter, recipe);
//                    });
//                    //then
//                    assertEquals("회원 id가 유효하지 않습니다.", exception.getMessage());
//                }
//            }
//
//            @Nested
//            @DisplayName("댓글 달 게시물 관련")
//            class RecipeFail {
//                @Test
//                @DisplayName("댓글 달 게시물이 null")
//                void RecipeNull(){
//                    //given
//                    recipe = null;
//                    //when
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content,commentWriter,recipe);
//                    });
//                    //then
//                    assertEquals("댓글 달 게시물이 존재하지 않습니다.",exception.getMessage());
//                }
//
//                @Test
//                @DisplayName("댓글 달 게시물 id 마이너스")
//                void RecipeIdMinus(){
//                    //given
//                    recipe = new Recipe(
//                            -1L,
//                            "test recipe title",
//                            "test recipe content",
//                            5000,
//                            recipeOwner
//                    );
//                    //when
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content,commentWriter,recipe);
//                    });
//                    //then
//                    assertEquals("댓글 달 게시물이 존재하지 않습니다.",exception.getMessage());
//                }
//            }
//
//            @Nested
//            @DisplayName("댓글 인풋 관련")
//            class CommentInputFail{
//                @Test
//                @DisplayName("빈 값")
//                void commentEmpty(){
//                    //given
//                    content = "";
//                    //when
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content,commentWriter,recipe);
//                    });
//                    //then
//                    assertEquals("댓글 내용이 입력되지 않았습니다.",exception.getMessage());
//                }
//
//                @Test
//                @DisplayName("null")
//                void commentNull(){
//                    //given
//                    content = null;
//                    //when
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content,commentWriter,recipe);
//                    });
//                    //then
//                    assertEquals("댓글 내용이 입력되지 않았습니다.",exception.getMessage());
//                }
//
//                @Test
//                @DisplayName("댓글내용 200자 초과")
//                void commentTooLong(){
//                    //given
//                    content = "What is Lorem Ipsum?\n" +
//                            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
//                            "\n" +
//                            "Why do we use it?\n" +
//                            "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
//                            "\n" +
//                            "\n" +
//                            "Where does it come from?\n" +
//                            "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.";
//                    Exception exception = assertThrows(IllegalArgumentException.class, ()->{
//                        RecipeComment comment = new RecipeComment(content,commentWriter,recipe);
//                    });
//                    //then
//                    assertEquals("댓글 내용이 200자를 초과하였습니다.",exception.getMessage());
//                }
//            }
//
//        }
//
//
//    }
//}