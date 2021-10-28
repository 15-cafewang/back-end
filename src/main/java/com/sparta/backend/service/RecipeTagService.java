//package com.sparta.backend.service;
//
//import com.sparta.backend.domain.Recipe;
//import com.sparta.backend.domain.RecipeTag;
//import com.sparta.backend.domain.Tag;
//import com.sparta.backend.repository.RecipeTagRepository;
//import com.sparta.backend.repository.RecipesRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class RecipeTagService {
//
//    private final RecipeTagRepository recipeTagRepository;
//
//    @Transactional
//    public List<RecipeTag> saveRecipeTag(Recipe savedRecipe, List<Tag> savedTagList) {
//        List<RecipeTag> recipeTagList = new ArrayList<>();
//        for(Tag tag : savedTagList){
//            RecipeTag recipeTag = new RecipeTag(savedRecipe,tag);
//            recipeTagList.add(recipeTag);
//        }
//
//        return recipeTagRepository.saveAll(recipeTagList);
//    }
//}
