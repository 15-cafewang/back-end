package com.sparta.backend.service;

import com.sparta.backend.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipesService {
    public List<Tag> saveTags(List<String> tagList) {
        for(String tag : tagList){

        }

    }
}
