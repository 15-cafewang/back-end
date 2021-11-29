package com.sparta.backend.service.cafe;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.cafe.*;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.dto.request.cafe.CafeRequestDto;
import com.sparta.backend.dto.request.cafe.CafePutRequestDto;
import com.sparta.backend.dto.response.cafe.CafeDetailResponsetDto;
import com.sparta.backend.dto.response.cafe.CafeListResponseDto;
import com.sparta.backend.dto.response.cafe.CafeRecommendResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.cafe.*;
import com.sparta.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class CafeService {

    private final CafeRepository cafeRepository;
    private final CafeLikeRepository cafeLikeRepository;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";
    private final CafeImageRepository cafeImageRepository;
    private final CafeDetailCountRepository cafeDetailCountRepository;
    private final CafeSearchCountRepository cafeSearchCountRepository;
    private final TagRepository tagRepository;

    @Autowired
    public CafeService(
            CafeRepository cafeRepository,
            CafeLikeRepository cafeLikeRepository,
            AmazonS3Client amazonS3Client,
            S3Uploader s3Uploader,
            CafeImageRepository cafeImageRepository,
            CafeDetailCountRepository cafeDetailCountRepository,
            CafeSearchCountRepository cafeSearchCountRepository,
            TagRepository tagRepository
    ){
        this.cafeRepository = cafeRepository;
        this.cafeLikeRepository = cafeLikeRepository;
        this.amazonS3Client = amazonS3Client;
        this.s3Uploader = s3Uploader;
        this.cafeImageRepository = cafeImageRepository;
        this.cafeDetailCountRepository = cafeDetailCountRepository;
        this.cafeSearchCountRepository = cafeSearchCountRepository;
        this.tagRepository = tagRepository;
    }

    //카페 저장
    public Cafe saveCafe(CafeRequestDto requestDto, User user) throws IOException {

        List<String> imageUrlList= requestDto.getImage()[0].getSize() == 0L? null :uploadManyImagesToS3(requestDto, "cafeImage");
        //사진들 저장
        Cafe cafe = uploadManyImagesToDB(imageUrlList,requestDto,user);

        return cafeRepository.save(cafe);
    }

    //카페 삭제, 이미지도 삭제
    public void deleteCafe(Long cafeId) {
        Cafe foundCafe = cafeRepository.findById(cafeId).orElseThrow(()->
                new CustomErrorException("해당 아이디가 존재하지 않습니다")
        );
        //이미지 수만큼 S3에서도 삭제
        for(int i = 0; i< foundCafe.getCafeImagesList().size(); i++){
            CafeImage cafeImage = foundCafe.getCafeImagesList().get(i);
            if(cafeImage != null) deleteS3(cafeImage.getImage());
        }
        cafeRepository.deleteById(cafeId);
    }

    //섬네일저장
    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight){
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    //여러장의 이미지를 s3에 저장하는 기능
    public List<String> uploadManyImagesToS3(CafeRequestDto requestDto, String dirName) throws IOException {
        List<String> savedImages = new ArrayList<>();
//
//        todo://섬네일 저장
//        BufferedImage thumbNailImage = resizeImage(requestDto.getImage()[0],100,100);
//
        //s3에 이미지저장
        for(MultipartFile img : requestDto.getImage()){
            if(img.isEmpty()) return savedImages;

            String imageUrl = s3Uploader.upload(img, dirName);
            if(imageUrl == null) throw new NullPointerException("이미지를 s3에 업로드하는 과정 실패");
            savedImages.add(imageUrl);
        }
        return savedImages;
    }
    public List<String> uploadManyImagesToS3(CafePutRequestDto requestDto, String dirName) throws IOException {
        List<String> savedImages = new ArrayList<>();
        if(requestDto.getImage() == null) return savedImages;
        //s3에 이미지저장
        for(MultipartFile img : requestDto.getImage()){
            System.out.println("빈 이미지??:"+img);
            if(img.isEmpty()) return savedImages;

            String imageUrl = s3Uploader.upload(img, dirName);
            if(imageUrl == null) throw new NullPointerException("이미지를 s3에 업로드하는 과정 실패");
            savedImages.add(imageUrl);
        }
        return savedImages;
    }
    //여러장의 이미지를 db에 저장하는 기능
    public Cafe uploadManyImagesToDB(List<String> imageUrlList, CafeRequestDto requestDto, User user){
        Cafe cafe = new Cafe(requestDto.getTitle(),requestDto.getContent(),requestDto.getLocation(),user);
        //디비에 이미지url저장
        if(imageUrlList!=null){
            List<CafeImage> cafeImages = new ArrayList<>();
            imageUrlList.forEach((image)-> cafeImages.add(new CafeImage(image, cafe)));
            cafeImageRepository.saveAll(cafeImages);
        }
//        System.out.println(cafe.getCafeImagesList());
//        System.out.println(cafe.getCafeImagesList().get(0).getImage());
        return cafe;
    }

    //카페 수정
    //todo: 문제점: 중간에 익셉션 터져서 롤백한 상황이라면, S3에서 수정한건 롤백이 안된다.
    @Transactional
    public Cafe updateCafe(Long cafeId, CafePutRequestDto requestDto) throws IOException {
        //게시글 존재여부확인
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(()->new CustomErrorException("해당 게시물을 찾을 수 없습니다"));

        //사진 총 5장 안넘는지 검사
        checkIfImageMoreThan5(cafe, requestDto);

        //수정할 이미지 S3에 업로드
        List<String> imageUrlList = uploadManyImagesToS3(requestDto,"cafeImage");

        //삭제하려는 사진이 해당게시물의 사진이 맞는지 검사- url만 가지고 다른게시물의 사진을 삭제 못하도록
        checkDeleteImageOwnership(cafeId, requestDto);

        //DB의 cafe_image 기존 row들 삭제(그냥 update하면 더 작은 개수로 image업뎃할때 outOfInedex에러남)
        if(requestDto.getDeleteImage() != null) cafeImageRepository.deleteByImageIn(requestDto.getDeleteImage());


        //cafe_image db에 저장
        if (imageUrlList.size()>0){
            List<CafeImage> cafeImageList = new ArrayList<>();
            imageUrlList.forEach((image)-> cafeImageList.add(new CafeImage(image, cafe)));
            cafeImageRepository.saveAll(cafeImageList);
        }

        //이미지 외 다른 내용들 수정
        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        String location = requestDto.getLocation();

        Cafe updatedCafe = cafe.updateCafe(title,content,location);

        //사진 다 수정되면 기존 사진 s3삭제 -> 중간에 작업하다가 익셉션 터지면 s3에 작업한 건 롤백이 안되니까 일부러 마지막에서 처리
        if(requestDto.getDeleteImage()!=null){
            for(int i=0; i<requestDto.getDeleteImage().size();i++){
                String s3Url = requestDto.getDeleteImage().get(i);
                if(s3Url!= null) deleteS3(s3Url);
            }
        }


        return updatedCafe;
    }

    //todo: n+1 문제 해결해야 할 듯
    private void checkDeleteImageOwnership(Long cafeId, CafePutRequestDto requestDto) {
        if(requestDto.getDeleteImage() == null ) return;
        List<String> imgUrls = requestDto.getDeleteImage();
        List<CafeImage> foundImages = cafeImageRepository.findByImageIn(imgUrls);
        foundImages.forEach(cafeImage -> {
            if(!cafeImage.getCafe().getId().equals(cafeId)) throw new IllegalArgumentException("해당 게시물의 사진만 수정할 수 있습니다.");
        });
    }

    private void checkIfImageMoreThan5(Cafe cafe, CafePutRequestDto requestDto) {
        int oldCount = cafe.getCafeImagesList().size();
        int deleteCount = 0;
        int addCount = 0;
        if(requestDto.getDeleteImage()!=null) deleteCount = requestDto.getDeleteImage().size();
        if(requestDto.getImage()!=null) addCount = requestDto.getImage().length;

        if(oldCount+addCount-deleteCount > 5 ) throw new IllegalArgumentException("사진은 총 5장 이상이 될 수 없습니다");
    }

    //S3 이미지 삭제
    public void deleteS3(@RequestParam String imageName){
        //https://S3 버킷 URL/버킷에 생성한 폴더명/이미지이름
        String keyName = "";
        try {keyName = imageName.split("/")[4]; // 이미지이름만 추출
        }catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("S3 url 형식이 아닙니다");
        }

        try {amazonS3Client.deleteObject(bucket + "/cafeImage", keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }

    //카페 상세조회
    public CafeDetailResponsetDto getCafeDetail(Long cafeId, UserDetailsImpl userDetails) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(()->
                new CustomErrorException("해당 게시물이 존재하지 않습니다"));

        saveClickDetailAction(cafe, userDetails);

        String nickname = cafe.getUser().getNickname();
        String title = cafe.getTitle();
        String content = cafe.getContent();
        LocalDateTime regDate = cafe.getRegDate();
        int likeCount = cafe.getCafeLikeList().size();
        String location = cafe.getLocation();
        String profile = cafe.getUser().getImage();
        Optional<CafeLike> foundCafeLike = cafeLikeRepository.findByCafeIdAndUserId(cafe.getId(),userDetails.getUser().getId());
        Boolean likeStatus = foundCafeLike.isPresent();

        List<String> tagNames = new ArrayList<>();
        cafe.getTagList().forEach((tag)->tagNames.add(tag.getName()));

        List<String> images =new ArrayList<>();
        cafe.getCafeImagesList().forEach((cafeImage)->images.add(cafeImage.getImage()));

        CafeDetailResponsetDto responsetDto = new CafeDetailResponsetDto(
                cafeId, nickname, title, content, regDate, likeCount, likeStatus, images, tagNames, location,profile);

        return responsetDto;
    }

    //상세보기 조횟수 등록
    private void saveClickDetailAction(Cafe cafe, UserDetailsImpl userDetails) {
        CafeDetailCount cafeDetailCount = new CafeDetailCount(userDetails.getUser(), cafe);
        cafeDetailCountRepository.save(cafeDetailCount);
    }

    public Page<CafeListResponseDto> getCafesByPage(int page, int size, boolean isAsc, String sortBy, Boolean sortByLike , UserDetailsImpl userDetails) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Cafe> cafes = sortByLike? cafeRepository.findCafesOrderByLikeCountDesc(pageable): cafeRepository.findAll(pageable);

        Page<CafeListResponseDto> responseDtos = cafes.map((cafe)->new CafeListResponseDto(cafe, userDetails, cafeLikeRepository));

        return responseDtos;
    }

    //좋아요 등록/취소
    public String likeCafe(Long postId, User user) {
        Cafe cafe = cafeRepository.findById(postId).orElseThrow(()->
                new CustomErrorException("해당 게시물이 존재하지 않아요"));
        //이미 좋아요누른 건지 확인하기
        Optional<CafeLike> foundCafeLike = cafeLikeRepository.findByCafeIdAndUserId(cafe.getId(),user.getId());
        if(foundCafeLike.isPresent()){
            //이미 좋아요를 눌렀으면 좋아요취소
            cafeLikeRepository.delete(foundCafeLike.get());
            return "좋아요 취소 성공";
        }else{
            CafeLike cafeLike = new CafeLike(user, cafe);
            cafeLikeRepository.save(cafeLike);
            return "좋아요 등록 성공";
        }

    }

    public Optional<Cafe> findById(Long cafeId) {
        return cafeRepository.findById(cafeId);
    }

    public Page<CafeListResponseDto> searchByTag(String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        page = page-1;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Cafe> cafes = cafeRepository.findAllByTag(keyword, pageable);
//        System.out.println(cafes.getContent().get(0));
        Page<CafeListResponseDto> responseDtos = cafes.map((cafe) -> new CafeListResponseDto(cafe,userDetails, cafeLikeRepository));
        return responseDtos;
    }

    public Page<CafeListResponseDto> searchByTitleOrContents(String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        page = page-1;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Cafe> cafes = cafeRepository.findAllByTitleOrContentOrLocationOrNickName(keyword, pageable);
        Page<CafeListResponseDto> responseDtos = cafes.map((cafe) -> new CafeListResponseDto(cafe,userDetails, cafeLikeRepository));
        return responseDtos;
    }

    //검색하기
    public Page<CafeListResponseDto> searchCafe(boolean withTag, String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {

        //검색 히스토리 등록
        if(withTag) saveSearchAction(keyword,userDetails.getUser());

        page = page-1;
        boolean isSortByLikeCount = false;
        if(sortBy.equals("likeCount")){
            sortBy = "regDate";
            isSortByLikeCount = true;
        }

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Cafe> cafes = null;
        keyword = keyword.trim();
        if(withTag && !isSortByLikeCount) cafes = cafeRepository.findAllByTag(keyword, pageable);
        if(withTag && isSortByLikeCount) cafes = cafeRepository.findAllByTagOrderByLikeCount(keyword,pageable);
        if(!withTag && !isSortByLikeCount) cafes = cafeRepository.findAllByTitleOrContentOrLocationOrNickName(keyword, pageable);
        if(!withTag && isSortByLikeCount) cafes = cafeRepository.findAllByTitleOrContentOrLocationOrderByLikeCount(keyword, pageable);
        Page<CafeListResponseDto> responseDtos = cafes.map((cafe) -> new CafeListResponseDto(cafe,userDetails, cafeLikeRepository));
        return responseDtos;
    }

    private void saveSearchAction(String keyword, User user) {
        CafeSearchCount cafeSearchCount = new CafeSearchCount(user, keyword);
        cafeSearchCountRepository.save(cafeSearchCount);
    }

    public List<CafeListResponseDto> getPopularCafe(String sortBy, User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = null;
        switch (sortBy){
            case "day":
                startDate = LocalDateTime.now().minusDays(1);
                break;
            case "week":
                startDate = LocalDateTime.now().minusWeeks(1);
                break;
            case "month":
                startDate = LocalDateTime.now().minusMonths(1);
                break;
            default: startDate = LocalDateTime.now().minusDays(1);
        }

        List<Long> popularCafeIdList = cafeRepository.findPopularCafeId2(startDate, now);
        List<Optional<Cafe>> popularCafeList = new ArrayList<>();
        popularCafeIdList.forEach((cafeId)-> popularCafeList.add(cafeRepository.findById(cafeId)));

        List<CafeListResponseDto> responseDtoList = new ArrayList<>();
        popularCafeList.forEach((cafe -> responseDtoList.add(new CafeListResponseDto(cafe, user, cafeLikeRepository))));

        return responseDtoList;
    }

    public List<CafeListResponseDto> getRecentCafe(User user) {
        List<Cafe> popularCafeIdList = cafeRepository.findTop3ByOrderByRegDateDesc();

        List<CafeListResponseDto> responseDtoList = new ArrayList<>();
        popularCafeIdList.forEach((cafe -> responseDtoList.add(new CafeListResponseDto(cafe, user, cafeLikeRepository))));
        return responseDtoList;
    }

    public List<CafeRecommendResponseDto> getRecommendedCafe(User user) {

        //0.현재 시간대 확인
        List<LocalDateTime> timeZone = getTimeZone(LocalDateTime.now());

        //1.해당 사용자의 기록이 존재하는지 체크
        System.out.println("시간확인:"+timeZone.get(0)+"//"+timeZone.get(1));
        List<Object[]> objectList= cafeRepository.checkUserHasData(user.getId(), timeZone.get(0),timeZone.get(1));
        boolean hasData =false;
        for(Object[] obj : objectList){
            if( (((BigInteger)obj[0]).intValue() >0 ) || (((BigInteger)obj[1]).intValue() >0 ) ||(((BigInteger)obj[2]).intValue() >0 ) ) hasData = true;
            System.out.println("되라:"+((BigInteger)obj[2]));
        }
        //2.존재하는 경우 태그 먼저 추출
        String foundTagName = (hasData)? cafeRepository.findRecommendingTagNameBasedOne(user.getId(), timeZone.get(0), timeZone.get(1))
                : cafeRepository.findRecommendingTagNameBasedAll(timeZone.get(0), timeZone.get(1));
        System.out.println("태그네임: "+foundTagName);

        //3.추출된 태그로 카페id추출
        Long foundCafeId = cafeRepository.findRecommendingCafeIdByTagName(foundTagName);
        //foundCafeId == null인 경우: 내가 검색한 태그명으로 나를 포함한 누구도 상세조회, 좋아요를 하지 않은 경우
        Cafe recommendedCafe = (foundCafeId == null)? cafeRepository.findRandomCafe()
        : cafeRepository.findById(foundCafeId).orElseThrow(()->new CustomErrorException("id로 해당 게시물 찾을 수 없음"));

        CafeRecommendResponseDto responseDto = new CafeRecommendResponseDto(recommendedCafe, foundTagName, user, cafeLikeRepository);
        return List.of(responseDto);
    }

    public List<LocalDateTime> getTimeZone(LocalDateTime now) {

        System.out.println("now.gethour:" + now.getHour());
        System.out.println(now.getHour() < 5);
        System.out.println(now);
        System.out.println(now.minusDays(1L));
        if( now.getHour() <5) now = now.minusDays(1L);

        LocalDateTime morningStart = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                5,0,0,0);
        LocalDateTime mornigEnd = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                11,0,0,0);
        LocalDateTime lunchStart = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                11,0,0,0);
        LocalDateTime lunchEnd = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                16,0,0,0);
        LocalDateTime dinnerStart = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                16,0,0,0);
        LocalDateTime dinnerEnd = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                20,0,0,0);
        LocalDateTime nightStart = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth(),
                20,0,0,0);
        LocalDateTime nightEnd = LocalDateTime.of(now.getYear(),
                now.getMonth(),
                now.getDayOfMonth()+1,
                4,0,0,0);



        if(now.isAfter(morningStart) && now.isBefore(mornigEnd)) return Arrays.asList(morningStart,mornigEnd);
        else if(now.isAfter(lunchStart) && now.isBefore(lunchEnd))  return Arrays.asList(lunchStart,lunchEnd);
        else if(now.isAfter(dinnerStart) && now.isBefore(dinnerEnd))  return Arrays.asList(dinnerStart,dinnerEnd);
        else return Arrays.asList(nightStart,nightEnd);

    }
}
