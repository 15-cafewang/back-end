package com.sparta.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class recipesController {
    private final RecipeService recipeService;

    @PostMapping("/api/recipes")
    public ResponseDto postRecipe(){

    }
}
