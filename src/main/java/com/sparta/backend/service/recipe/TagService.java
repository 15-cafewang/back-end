package com.sparta.backend.service.recipe;

import com.sparta.backend.domain.recipe.Recipe;
import com.sparta.backend.domain.recipe.Tag;
import com.sparta.backend.repository.recipe.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    //태그저장
    @Transactional
    public List<Tag> saveTags(List<String> tagList, Recipe recipe){
        List<Tag> tmp_tagList = new ArrayList<>();
        for(String tagName : tagList){
            Tag tag = new Tag(tagName, recipe);
            tmp_tagList.add(tag);
        }

        return tagRepository.saveAll(tmp_tagList);
    }

    //태그 업데이트
    @Transactional
    public List<Tag> updateTags(List<String> tagList, Recipe updatedRecipe) {
        //기존 태그 삭제
        tagRepository.deleteAllByRecipe(updatedRecipe);

        //새 태그 저장
        return saveTags(tagList, updatedRecipe);
    }
}
