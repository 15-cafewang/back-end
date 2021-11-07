package com.sparta.backend.service.Recipe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    //레시피 저장 테스트
        //권환관련
            //로그인 안 한사람이 저장시도- 실패
            //로그인 한사람이 저장시도- 성공
        //내용 유무관련
            //아무 내용 없는 레시피 - 실패
            //제목만 없는 레시피 - 실패
            //내용만 없는 레시피 - 실패
            //사진만 없는 레시피 - 성공
            //가격만 없는 레시피 - 서공
            //사진 5장 다 올린 레시피 - 성공
            //사진 5장 초과(6장,7장) 올린 레시피 - 실패
 

}