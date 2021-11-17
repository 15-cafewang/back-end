//package com.sparta.backend.testdata;
//
//
//import com.sparta.backend.domain.Recipe.Recipe;
//import com.sparta.backend.domain.Recipe.RecipeComment;
//import com.sparta.backend.domain.User;
//import com.sparta.backend.repository.recipe.RecipeCommentRepository;
//import com.sparta.backend.repository.recipe.RecipeRepository;
//import com.sparta.backend.repository.UserRepository;
//import com.sparta.backend.service.Recipe.RecipeService;
//import com.sparta.backend.service.Recipe.TagService;
//import com.sparta.backend.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
////@Component
//public class TestDataRunner implements ApplicationRunner {
//    @Autowired
//    PasswordEncoder passwordEncoder;
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    UserService userService;
//    @Autowired
//    RecipeRepository recipeRepository;
//    @Autowired
//    RecipeCommentRepository recipeCommentRepository;
//    @Autowired
//    RecipeService recipeService;
//    @Autowired
//    TagService tagService;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        User testUser1 = new User("aaa@gmail.com",passwordEncoder.encode("1234qwer"),"aaa");
//        User testUser2= new User("bbb@gmail.com",passwordEncoder.encode("1234qwer"),"bbb");
//        User testUser3 = new User("ccc@gmail.com",passwordEncoder.encode("1234qwer"),"ccc");
//
//
//        //회원가입
//        User user1 = userRepository.save(testUser1);
//        User user2 = userRepository.save(testUser2);
//        User user3 = userRepository.save(testUser3);
//
//        //레시피 등록
//        for(int i=1; i<100; i+=3){
//            더미레시피올리기("더미"+i+"의 제목",
//                    "aaa의 내용입니다 울루루루",
//                    5000,
//                    "s3://99final/recipeImage/ff162957-df02-471a-bb78-69a78e8447e0puppy.jpg",
//                    new ArrayList<>(Arrays.asList("가가","나나","다다")),
//                    user1,user2,user3);
//            더미레시피올리기("더미"+(i+1)+"의 제목",
//                    "bbb의 내용입니다 하하하하",
//                    10000,
//                    "s3://99final/recipeImage/ff162957-df02-471a-bb78-69a78e8447e0puppy.jpg",
//                    new ArrayList<>(Arrays.asList("가가","나나")),
//                    user2,user2,user3);
//            더미레시피올리기("더미"+(i+2)+"의 제목",
//                    "ccc의 내용입니다 쿄쿄쿄쿄",
//                    20000,
//                    "",
//                    new ArrayList<>(Arrays.asList("가가")),
//                    user3,user2,user3);
//        }
//
//        //좋아요 등록
//        recipeService.likeRecipe(1L, user2);
//        recipeService.likeRecipe(1L, user3);
//        recipeService.likeRecipe(3L, user1);
//        recipeService.likeRecipe(4L, user3);
//        recipeService.likeRecipe(4L, user2);
//        recipeService.likeRecipe(5L, user1);
//
//
//
//    }
//
//
//    private void 더미레시피올리기(String title, String content, int price, String image, List<String> tags,User user, User commentWriter1, User commentWriter2) {
//        Recipe recipe = new Recipe(title, content, price, image, user);
//        Recipe saveRecipe = recipeRepository.save(recipe);
//        tagService.saveTags(tags,saveRecipe);
//        //1번유저가 댓글달기
//        if(user.getNickname().equals("aaa")){
//            for(int i=0; i<6; i++){
//                더미레시피댓글달기("bbb가 쓴 더미댓글입니다",commentWriter1, recipe);
//                더미레시피댓글달기("ccc가 쓴 더미댓글입니다",commentWriter2, recipe);
//            }
//        }
//        if(user.getNickname().equals("bbb")){
//            for(int i=0; i<6; i++){
//                더미레시피댓글달기("aaa가 쓴 더미댓글입니다",commentWriter1, recipe);
//                더미레시피댓글달기("ccc가 쓴 더미댓글입니다",commentWriter2, recipe);
//            }
//        }
//        if(user.getNickname().equals("ccc")){
//            for(int i=0; i<6; i++){
//                더미레시피댓글달기("aaa가 쓴 더미댓글입니다",commentWriter1, recipe);
//                더미레시피댓글달기("bbb가 쓴 더미댓글입니다",commentWriter2, recipe);
//            }
//        }
//    }
//
//    private void 더미레시피댓글달기(String content, User user, Recipe recipe){
//        RecipeComment recipeComment = new RecipeComment(content, user, recipe);
//        recipeCommentRepository.save(recipeComment);
//    }
//
//
//}