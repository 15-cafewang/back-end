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

import java.awt.image.BufferedImage;
import java.io.File;
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

    //?????? ??????
    public Cafe saveCafe(CafeRequestDto requestDto, User user) throws IOException {

        //???????????? S3??? ??????
        List<String> imageUrlList= requestDto.getImage()[0].getSize() == 0L? null :uploadManyImagesToS3(requestDto, "cafeImage");

        //???????????? S3??? ??????
        String thumbNailUrl = requestDto.getImage()[0].getSize() == 0L? null:uploadThumbNailImageToS3(requestDto.getImage()[0], "thumbNail");

        //???????????? db??? ??????
        Cafe cafe = uploadManyImagesToDB(imageUrlList,requestDto,user,thumbNailUrl);

        return cafeRepository.save(cafe);
    }

    //?????? ??????, ???????????? ??????
    public void deleteCafe(Long cafeId) {
        Cafe foundCafe = cafeRepository.findById(cafeId).orElseThrow(()->
                new CustomErrorException("?????? ???????????? ???????????? ????????????")
        );
        //????????? ????????? S3????????? ??????
        for(int i = 0; i< foundCafe.getCafeImagesList().size(); i++){
            CafeImage cafeImage = foundCafe.getCafeImagesList().get(i);
            if(cafeImage != null) deleteS3(cafeImage.getImage(), "/cafeImage");
        }
        cafeRepository.deleteById(cafeId);
    }


    private String uploadThumbNailImageToS3(MultipartFile multipartFile, String dirName) throws IOException {
        String savedImageUrl = s3Uploader.resizeAndUpload(multipartFile, dirName);
        if(savedImageUrl == null) throw new NullPointerException("???????????? s3??? ??????????????? ?????? ??????");
        return savedImageUrl;
    }

    //???????????? ???????????? s3??? ???????????? ??????
    public List<String> uploadManyImagesToS3(CafeRequestDto requestDto, String dirName) throws IOException {
        List<String> savedImages = new ArrayList<>();

        //s3??? ???????????????
        for(MultipartFile img : requestDto.getImage()){
            if(img.isEmpty()) return savedImages;

            String imageUrl = s3Uploader.upload(img, dirName);
            if(imageUrl == null) throw new NullPointerException("???????????? s3??? ??????????????? ?????? ??????");
            savedImages.add(imageUrl);
        }
        return savedImages;
    }
    public List<String> uploadManyImagesToS3(CafePutRequestDto requestDto, String dirName) throws IOException {
        List<String> savedImages = new ArrayList<>();
        if(requestDto.getImage() == null) return savedImages;
        //s3??? ???????????????
        for(MultipartFile img : requestDto.getImage()){
            if(img.isEmpty()) return savedImages;

            String imageUrl = s3Uploader.upload(img, dirName);
            if(imageUrl == null) throw new NullPointerException("???????????? s3??? ??????????????? ?????? ??????");
            savedImages.add(imageUrl);
        }
        return savedImages;
    }
    //???????????? ???????????? db??? ???????????? ??????
    public Cafe uploadManyImagesToDB(List<String> imageUrlList, CafeRequestDto requestDto, User user,String thumbNailUrl){
        Cafe cafe = new Cafe(requestDto.getTitle(),requestDto.getContent(),requestDto.getLocation(),user,thumbNailUrl);
        //????????? ?????????url??????
        if(imageUrlList!=null){
            List<CafeImage> cafeImages = new ArrayList<>();
            imageUrlList.forEach((image)-> cafeImages.add(new CafeImage(image, cafe)));
            cafeImageRepository.saveAll(cafeImages);
        }
        return cafe;
    }

    //?????? ??????
    //todo: ?????????: ????????? ????????? ????????? ????????? ???????????????, S3?????? ???????????? ????????? ?????????.
    @Transactional
    public Cafe updateCafe(Long cafeId, CafePutRequestDto requestDto) throws IOException {
        //????????? ??????????????????
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(()->new CustomErrorException("?????? ???????????? ?????? ??? ????????????"));

        //?????? ??? 5??? ???????????? ??????
        checkIfImageMoreThan5(cafe, requestDto);

        //????????? ????????? S3??? ?????????
        List<String> imageUrlList = uploadManyImagesToS3(requestDto,"cafeImage");

        //??????????????? ????????? ?????????????????? ????????? ????????? ??????- url??? ????????? ?????????????????? ????????? ?????? ????????????
        checkDeleteImageOwnership(cafeId, requestDto);

        //DB??? cafe_image ?????? row??? ??????(?????? update?????? ??? ?????? ????????? image???????????? outOfInedex?????????)
        if(requestDto.getDeleteImage() != null) cafeImageRepository.deleteByImageIn(requestDto.getDeleteImage());

        //cafe_image db??? ??????
        if (imageUrlList.size()>0){
            List<CafeImage> cafeImageList = new ArrayList<>();
            imageUrlList.forEach((image)-> cafeImageList.add(new CafeImage(image, cafe)));
            cafeImageRepository.saveAll(cafeImageList);
        }

        //????????? ??? ?????? ????????? ??????
        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        String location = requestDto.getLocation();
        String oldThumbNailUrl = cafe.getThumbNailImage();

        //?????? ??? ???????????? ?????? ?????? s3?????? -> ????????? ??????????????? ????????? ????????? s3??? ????????? ??? ????????? ???????????? ????????? ??????????????? ??????
        if(requestDto.getDeleteImage()!=null){
            for(int i=0; i<requestDto.getDeleteImage().size();i++){
                String s3Url = requestDto.getDeleteImage().get(i);
                if(s3Url!= null) deleteS3(s3Url,"/cafeImage");
            }
        }

        //????????? ????????? ?????????
        CafeImage cafeImage = cafeImageRepository.findTopByCafe(cafe);
        BufferedImage resizedImage = s3Uploader.resizeImage(cafeImage.getImage());

        File imageFile = new File(cafeImage.getImage());
        String savedThumbNailUrl = s3Uploader.uploadBufferedImageToS3(resizedImage,"thumbNail",imageFile);

        Cafe updatedCafe = cafe.updateCafe(title,content,location,savedThumbNailUrl);
        //?????? ???????????? s3?????? ??????
        deleteS3(oldThumbNailUrl,"/thumbNail");

        return updatedCafe;
    }

    //todo: n+1 ?????? ???????????? ??? ???
    private void checkDeleteImageOwnership(Long cafeId, CafePutRequestDto requestDto) {
        if(requestDto.getDeleteImage() == null ) return;
        List<String> imgUrls = requestDto.getDeleteImage();
        List<CafeImage> foundImages = cafeImageRepository.findByImageIn(imgUrls);
        foundImages.forEach(cafeImage -> {
            if(!cafeImage.getCafe().getId().equals(cafeId)) throw new IllegalArgumentException("?????? ???????????? ????????? ????????? ??? ????????????.");
        });
    }

    private void checkIfImageMoreThan5(Cafe cafe, CafePutRequestDto requestDto) {
        int oldCount = cafe.getCafeImagesList().size();
        int deleteCount = 0;
        int addCount = 0;
        if(requestDto.getDeleteImage()!=null) deleteCount = requestDto.getDeleteImage().size();
        if(requestDto.getImage()!=null) addCount = requestDto.getImage().length;

        if(oldCount+addCount-deleteCount > 5 ) throw new IllegalArgumentException("????????? ??? 5??? ????????? ??? ??? ????????????");
    }

    //S3 ????????? ??????
    public void deleteS3(@RequestParam String imageName,String dir){
        //https://S3 ?????? URL/????????? ????????? ?????????/???????????????
        String keyName = "";
        try {keyName = imageName.split("/")[4]; // ?????????????????? ??????
        }catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("S3 url ????????? ????????????");
        }

        try {amazonS3Client.deleteObject(bucket + dir, keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }

    //?????? ????????????
    public CafeDetailResponsetDto getCafeDetail(Long cafeId, UserDetailsImpl userDetails) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(()->
                new CustomErrorException("?????? ???????????? ???????????? ????????????"));

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
        int rankingStatus = cafe.getUser().getRankingStatus();

        List<String> tagNames = new ArrayList<>();
        cafe.getTagList().forEach((tag)->tagNames.add(tag.getName()));

        List<String> images =new ArrayList<>();
        cafe.getCafeImagesList().forEach((cafeImage)->images.add(cafeImage.getImage()));

        CafeDetailResponsetDto responsetDto = new CafeDetailResponsetDto(
                cafeId, nickname, title, content, regDate, likeCount, likeStatus, images, tagNames, location,profile,rankingStatus);

        return responsetDto;
    }

    //???????????? ????????? ??????
    private void saveClickDetailAction(Cafe cafe, UserDetailsImpl userDetails) {
        CafeDetailCount cafeDetailCount = new CafeDetailCount(userDetails.getUser(), cafe);
        cafeDetailCountRepository.save(cafeDetailCount);
    }

    public Page<CafeListResponseDto> getCafesByPage(int page, int size, boolean isAsc, String sortBy, Boolean sortByLike , UserDetailsImpl userDetails) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Cafe> cafes = sortByLike? cafeRepository.findCafesOrderByLikeCountDesc(pageable): cafeRepository.findAll(pageable);

        Page<CafeListResponseDto> responseDtos = cafes.map((cafe)->new CafeListResponseDto(cafe, userDetails, cafeLikeRepository, s3Uploader));

        return responseDtos;
    }

    //????????? ??????/??????
    public String likeCafe(Long postId, User user) {
        Cafe cafe = cafeRepository.findById(postId).orElseThrow(()->
                new CustomErrorException("?????? ???????????? ???????????? ?????????"));
        //?????? ??????????????? ?????? ????????????
        Optional<CafeLike> foundCafeLike = cafeLikeRepository.findByCafeIdAndUserId(cafe.getId(),user.getId());
        if(foundCafeLike.isPresent()){
            //?????? ???????????? ???????????? ???????????????
            cafeLikeRepository.delete(foundCafeLike.get());
            return "????????? ?????? ??????";
        }else{
            CafeLike cafeLike = new CafeLike(user, cafe);
            cafeLikeRepository.save(cafeLike);
            return "????????? ?????? ??????";
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
        Page<CafeListResponseDto> responseDtos = cafes.map((cafe) -> new CafeListResponseDto(cafe,userDetails, cafeLikeRepository,s3Uploader));
        return responseDtos;
    }

    public Page<CafeListResponseDto> searchByTitleOrContents(String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        page = page-1;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Cafe> cafes = cafeRepository.findAllByTitleOrContentOrLocationOrNickName(keyword, pageable);
        Page<CafeListResponseDto> responseDtos = cafes.map((cafe) -> new CafeListResponseDto(cafe,userDetails, cafeLikeRepository,s3Uploader));
        return responseDtos;
    }

    //????????????
    public Page<CafeListResponseDto> searchCafe(boolean withTag, String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {

        //?????? ???????????? ??????
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
        Page<CafeListResponseDto> responseDtos = cafes.map((cafe) -> new CafeListResponseDto(cafe,userDetails, cafeLikeRepository,s3Uploader));
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
        popularCafeList.forEach((cafe -> responseDtoList.add(new CafeListResponseDto(cafe, user, cafeLikeRepository, s3Uploader))));

        return responseDtoList;
    }

    public List<CafeListResponseDto> getRecentCafe(User user) {
        List<Cafe> popularCafeIdList = cafeRepository.findTop3ByOrderByRegDateDesc();

        List<CafeListResponseDto> responseDtoList = new ArrayList<>();
        popularCafeIdList.forEach((cafe -> responseDtoList.add(new CafeListResponseDto(cafe, user, cafeLikeRepository, s3Uploader))));
        return responseDtoList;
    }

    public List<CafeRecommendResponseDto> getRecommendedCafe(User user) {

        //0.?????? ????????? ??????
        List<LocalDateTime> timeZone = getTimeZone(LocalDateTime.now());

        //1.?????? ???????????? ????????? ??????????????? ??????
        System.out.println("????????????:"+timeZone.get(0)+"//"+timeZone.get(1));
        List<Object[]> objectList= cafeRepository.checkUserHasData(user.getId(), timeZone.get(0),timeZone.get(1));
        boolean hasData =false;
        for(Object[] obj : objectList){
            if( (((BigInteger)obj[0]).intValue() >0 ) || (((BigInteger)obj[1]).intValue() >0 ) ||(((BigInteger)obj[2]).intValue() >0 ) ) hasData = true;
            System.out.println("??????:"+((BigInteger)obj[2]));
        }
        //2.???????????? ?????? ?????? ?????? ??????
        String foundTagName = (hasData)? cafeRepository.findRecommendingTagNameBasedOne(user.getId(), timeZone.get(0), timeZone.get(1))
                : cafeRepository.findRecommendingTagNameBasedAll(timeZone.get(0), timeZone.get(1));
        System.out.println("????????????: "+foundTagName);

        //3.????????? ????????? ??????id??????
        Long foundCafeId = cafeRepository.findRecommendingCafeIdByTagName(foundTagName);
        //foundCafeId == null??? ??????: ?????? ????????? ??????????????? ?????? ????????? ????????? ????????????, ???????????? ?????? ?????? ??????
        Cafe recommendedCafe = (foundCafeId == null)? cafeRepository.findRandomCafe()
        : cafeRepository.findById(foundCafeId).orElseThrow(()->new CustomErrorException("id??? ?????? ????????? ?????? ??? ??????"));

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
