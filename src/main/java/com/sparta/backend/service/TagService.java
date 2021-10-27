package com.sparta.backend.service;

import com.sparta.backend.domain.Tag;
import com.sparta.backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    public List<Tag> saveTags(List<String> tagList){
        List<Tag> tmp_tagList = new ArrayList<>();
        for(String tag : tagList){
            Tag tagObj = new Tag(tag);
            tmp_tagList.add(tagObj);
        }

        return tagRepository.saveAll(tmp_tagList);
    }
}
