package com.sparta.backend.service.cafe;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.Tag;
import com.sparta.backend.repository.cafe.TagRepository;
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
    public List<Tag> saveTags(List<String> tagList, Cafe cafe){
        List<Tag> tmp_tagList = new ArrayList<>();
        for(String tagName : tagList){
            Tag tag = new Tag(tagName, cafe);
            tmp_tagList.add(tag);
        }

        return tagRepository.saveAll(tmp_tagList);
    }

    //태그 업데이트
    @Transactional
    public List<Tag> updateTags(List<String> tagList, Cafe updatedCafe) {
        //기존 태그 삭제
        tagRepository.deleteAllByCafe(updatedCafe);

        //새 태그 저장
        return saveTags(tagList, updatedCafe);
    }
}
