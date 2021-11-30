package com.sparta.backend.controller.cafe;

import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.user.UserRole;
import com.sparta.backend.dto.request.cafe.CafeRequestDto;
import com.sparta.backend.dto.request.cafe.CafePutRequestDto;
import com.sparta.backend.dto.response.CustomResponseDto;
import com.sparta.backend.dto.response.cafe.CafeDetailResponsetDto;
import com.sparta.backend.dto.response.cafe.CafeListResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.service.cafe.CafeService;
import com.sparta.backend.service.cafe.TagService;
import com.sparta.backend.validator.PostCafeRequestDtoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CafeController {
    private final CafeService cafeService;
    private final TagService tagService;

    //카페 등록
    @PostMapping("/cafes")
    public ResponseEntity<?> postCafe(CafeRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //todo:IOException처리
        checkLogin(userDetails);
        PostCafeRequestDtoValidator.validateCafeInput(requestDto);

//        카페 먼저 생성, 등록
        Cafe savedCafe = cafeService.saveCafe(requestDto, userDetails.getUser());
//        태그 등록할때 저장한 카페객체도 넣어줌
        tagService.saveTags(requestDto.getTag(), savedCafe);

        return new ResponseEntity<>(new CustomResponseDto<>(1, "카페 등록 성공", ""), HttpStatus.OK);
    }

    //카페 수정
    @PutMapping("/cafes/{cafeId}")
    public ResponseEntity<?> updateCafe(@PathVariable Long cafeId, CafePutRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        //todo:IOException처리
        checkLogin(userDetails);
        checkOwnership(cafeId, userDetails);

        //카페 업데이트
        Cafe updatedCafe = cafeService.updateCafe(cafeId,requestDto);
        //태그 업데이트
        tagService.updateTags(requestDto.getTag(), updatedCafe);

        return new ResponseEntity<>(new CustomResponseDto<>(1, "카페 수정 성공", ""),HttpStatus.OK);
    }

    //카페 삭제
    @DeleteMapping("cafes/{cafeId}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long cafeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        checkLogin(userDetails);
        checkOwnership(cafeId, userDetails);

        cafeService.deleteCafe(cafeId);
        return new ResponseEntity<>(new CustomResponseDto<>(1, "카페 삭제 성공", ""),HttpStatus.OK);
    }

    //카페 상세조회
    @GetMapping("cafes/{cafeId}")
    public ResponseEntity<?> getCafeDetail(@PathVariable Long cafeId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        CafeDetailResponsetDto cafeDetailResponsetDto = cafeService.getCafeDetail(cafeId, userDetails);
        return new ResponseEntity<>(new CustomResponseDto<>(1, "카페 조회 성공", cafeDetailResponsetDto),HttpStatus.OK);
    }

    //카페 목록조회
    @GetMapping("cafes/list")
    public ResponseEntity<?> getCafes(@RequestParam("page") int page,
                                           @RequestParam("size") int size,
                                           @RequestParam("isAsc") boolean isAsc,
                                           @RequestParam("sortBy") String sortBy,
                                           @RequestParam("sortByLike") Boolean sortByLike,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        page = page-1;
        Page<CafeListResponseDto> cafesByPage = cafeService.getCafesByPage(page, size, isAsc, sortBy,sortByLike, userDetails);
        return new ResponseEntity<>(new CustomResponseDto<>(1, "카페 리스트 성공", cafesByPage),HttpStatus.OK);
    }

    //카페 좋아요 등록/취소
    @GetMapping("cafes/likes/{postId}")
    public ResponseEntity<?> likeCafe(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        String resultMessage = cafeService.likeCafe(postId, userDetails.getUser());
        return new ResponseEntity<>(new CustomResponseDto<>(1, resultMessage,""),HttpStatus.OK);
    }

    //카페 검색
    @GetMapping("/search/cafe")
    public ResponseEntity<?> searchCafe(@RequestParam("keyword") String keyword,
                                             @RequestParam("withTag") boolean withTag,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size,
                                             @RequestParam("isAsc") boolean isAsc,
                                             @RequestParam("sortBy") String sortBy,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        checkLogin(userDetails);
        Page<CafeListResponseDto> cafeByPage= cafeService.searchCafe(withTag,keyword, page, size, isAsc, sortBy,userDetails);
        return new ResponseEntity<>(new CustomResponseDto<>(1, "카페 리스트 성공", cafeByPage),HttpStatus.OK);
    }

    private void checkLogin(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomErrorException("로그인된 유저만 사용가능한 기능입니다.");
        }
    }

    private void checkOwnership(Long cafeId, UserDetailsImpl userDetails){
        Optional<Cafe> cafe = cafeService.findById(cafeId);
        if(cafe.isEmpty()) throw new NoSuchElementException("해당 게시물이 존재하지 않습니다");
        if(!cafe.get().getUser().getEmail().equals(userDetails.getUser().getEmail()) && userDetails.getUser().getRole() != UserRole.ADMIN)
            throw new CustomErrorException("본인의 게시물만 수정,삭제 가능합니다.");
    }
}

