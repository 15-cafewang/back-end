package com.sparta.backend.service.recipe;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.recipe.*;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.request.recipes.PutRecipeRequestDto;
import com.sparta.backend.dto.response.recipes.RecipeDetailResponsetDto;
import com.sparta.backend.dto.response.recipes.RecipeListResponseDto;
import com.sparta.backend.dto.response.recipes.RecipeRecommendResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.recipe.*;
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

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeLikesRepository recipeLikesRepository;
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3Client;
    private final String bucket = "99final";
    private final RecipeImageRepository recipeImageRepository;
    private final RecipeDetailCountRepository recipeDetailCountRepository;
    private final RecipeSearchCountRepository recipeSearchCountRepository;
    private final TagRepository tagRepository;

    @Autowired
    public RecipeService(
            RecipeRepository recipeRepository,
            RecipeLikesRepository recipeLikesRepository,
            AmazonS3Client amazonS3Client,
            S3Uploader s3Uploader,
            RecipeImageRepository recipeImageRepository,
            RecipeDetailCountRepository recipeDetailCountRepository,
            RecipeSearchCountRepository recipeSearchCountRepository,
            TagRepository tagRepository
    ){
        this.recipeRepository = recipeRepository;
        this.recipeLikesRepository = recipeLikesRepository;
        this.amazonS3Client = amazonS3Client;
        this.s3Uploader = s3Uploader;
        this.recipeImageRepository = recipeImageRepository;
        this.recipeDetailCountRepository = recipeDetailCountRepository;
        this.recipeSearchCountRepository = recipeSearchCountRepository;
        this.tagRepository = tagRepository;
    }

    //레시피 저장
    public Recipe saveRecipe(PostRecipeRequestDto requestDto, User user) throws IOException {

        List<String> imageUrlList= requestDto.getImage()[0].getSize() == 0L? null :uploadManyImagesToS3(requestDto, "recipeImage");
        Recipe recipe = uploadManyImagesToDB(imageUrlList,requestDto,user);
        return recipeRepository.save(recipe);
    }

    //레시피 삭제, 이미지도 삭제
    public void deleteRecipe(Long recipeId) {
        Recipe foundRecipe = recipeRepository.findById(recipeId).orElseThrow(()->
                new CustomErrorException("해당 아이디가 존재하지 않습니다")
        );
        //이미지 수만큼 S3에서도 삭제
        for(int i=0; i<foundRecipe.getRecipeImagesList().size();i++){
            RecipeImage recipeImage = foundRecipe.getRecipeImagesList().get(i);
            if(recipeImage!= null) deleteS3(recipeImage.getImage());
        }
        recipeRepository.deleteById(recipeId);
    }

    //여러장의 이미지를 s3에 저장하는 기능
    public List<String> uploadManyImagesToS3(PostRecipeRequestDto requestDto, String dirName) throws IOException {
        List<String> savedImages = new ArrayList<>();
        //s3에 이미지저장
        for(MultipartFile img : requestDto.getImage()){
            if(img.isEmpty()) return savedImages;

            String imageUrl = s3Uploader.upload(img, dirName);
            if(imageUrl == null) throw new NullPointerException("이미지를 s3에 업로드하는 과정 실패");
            savedImages.add(imageUrl);
        }
        return savedImages;
    }
    public List<String> uploadManyImagesToS3(PutRecipeRequestDto requestDto, String dirName) throws IOException {
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
    public Recipe uploadManyImagesToDB(List<String> imageUrlList, PostRecipeRequestDto requestDto, User user){
        Recipe recipe = new Recipe(requestDto.getTitle(),requestDto.getContent(),requestDto.getPrice(),user);
        //디비에 이미지url저장
        if(imageUrlList!=null){
            List<RecipeImage> recipeImages = new ArrayList<>();
            imageUrlList.forEach((image)-> recipeImages.add(new RecipeImage(image,recipe)));
            recipeImageRepository.saveAll(recipeImages);
        }
//        System.out.println(recipe.getRecipeImagesList());
//        System.out.println(recipe.getRecipeImagesList().get(0).getImage());
        return recipe;
    }

    //레시피 수정
    //todo: 문제점: 중간에 익셉션 터져서 롤백한 상황이라면, S3에서 수정한건 롤백이 안된다.
    @Transactional
    public Recipe updateRecipe(Long recipeId,PutRecipeRequestDto requestDto) throws IOException {
        //게시글 존재여부확인
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()->new CustomErrorException("해당 게시물을 찾을 수 없습니다"));

        //사진 총 5장 안넘는지 검사
        checkIfImageMoreThan5(recipe, requestDto);

        //수정할 이미지 S3에 업로드
        List<String> imageUrlList = uploadManyImagesToS3(requestDto,"recipeImage");

        //삭제하려는 사진이 해당게시물의 사진이 맞는지 검사- url만 가지고 다른게시물의 사진을 삭제 못하도록
        checkDeleteImageOwnership(recipeId, requestDto);

        //DB의 recipe_image 기존 row들 삭제(그냥 update하면 더 작은 개수로 image업뎃할때 outOfInedex에러남)
        if(requestDto.getDeleteImage() != null) recipeImageRepository.deleteByImageIn(requestDto.getDeleteImage());


        //recipe_image db에 저장
        if (imageUrlList.size()>0){
            List<RecipeImage> recipeImageList = new ArrayList<>();
            imageUrlList.forEach((image)->recipeImageList.add(new RecipeImage(image,recipe)));
            recipeImageRepository.saveAll(recipeImageList);
        }

        //이미지 외 다른 내용들 수정
        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        Integer price = requestDto.getPrice();

        Recipe updatedRecipe = recipe.updateRecipe(title,content,price);

        //사진 다 수정되면 기존 사진 s3삭제 -> 중간에 작업하다가 익셉션 터지면 s3에 작업한 건 롤백이 안되니까 일부러 마지막에서 처리
        if(requestDto.getDeleteImage()!=null){
            for(int i=0; i<requestDto.getDeleteImage().size();i++){
                String s3Url = requestDto.getDeleteImage().get(i);
                if(s3Url!= null) deleteS3(s3Url);
            }
        }


        return updatedRecipe;
    }

    //todo: n+1 문제 해결해야 할 듯
    private void checkDeleteImageOwnership(Long recipeId, PutRecipeRequestDto requestDto) {
        if(requestDto.getDeleteImage() == null ) return;
        List<String> imgUrls = requestDto.getDeleteImage();
        List<RecipeImage> foundImages = recipeImageRepository.findByImageIn(imgUrls);
        foundImages.forEach(recipeImage -> {
            if(!recipeImage.getRecipe().getId().equals(recipeId)) throw new IllegalArgumentException("해당 게시물의 사진만 수정할 수 있습니다.");
        });
    }

    private void checkIfImageMoreThan5(Recipe recipe, PutRecipeRequestDto requestDto) {
        int oldCount = recipe.getRecipeImagesList().size();
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

        try {amazonS3Client.deleteObject(bucket + "/recipeImage", keyName);
        }catch (AmazonServiceException e){
            e.printStackTrace();
            throw new AmazonServiceException(e.getMessage());
        }
    }

    //레시피 상세조회
    public RecipeDetailResponsetDto getRecipeDetail(Long recipeId, UserDetailsImpl userDetails) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()->
                new CustomErrorException("해당 게시물이 존재하지 않습니다"));

        saveClickDetailAction(recipe, userDetails);

        String nickname = recipe.getUser().getNickname();
        String title = recipe.getTitle();
        String content = recipe.getContent();
        LocalDateTime regDate = recipe.getRegDate();
        int likeCount = recipe.getRecipeLikesList().size();
        Integer price = recipe.getPrice();
        String profile = recipe.getUser().getImage();
        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository.findByRecipeIdAndUserId(recipe.getId(),userDetails.getUser().getId());
        Boolean likeStatus = foundRecipeLike.isPresent();

        List<String> tagNames = new ArrayList<>();
        recipe.getTagList().forEach((tag)->tagNames.add(tag.getName()));

        List<String> images =new ArrayList<>();
        recipe.getRecipeImagesList().forEach((recipeImage)->images.add(recipeImage.getImage()));

        RecipeDetailResponsetDto responsetDto = new RecipeDetailResponsetDto(
                recipeId, nickname, title, content, regDate, likeCount, likeStatus, images, tagNames, price,profile);

        return responsetDto;
    }

    //상세보기 조횟수 등록
    private void saveClickDetailAction(Recipe recipe, UserDetailsImpl userDetails) {
        RecipeDetailCount recipeDetailCount = new RecipeDetailCount(userDetails.getUser(), recipe);
        recipeDetailCountRepository.save(recipeDetailCount);
    }

    public Page<RecipeListResponseDto> getRecipesByPage(int page, int size, boolean isAsc, String sortBy, Boolean sortByLike ,UserDetailsImpl userDetails) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Recipe> recipes = sortByLike? recipeRepository.findRecipesOrderByLikeCountDesc(pageable): recipeRepository.findAll(pageable);

        Page<RecipeListResponseDto> responseDtos = recipes.map((recipe)->new RecipeListResponseDto(recipe, userDetails,recipeLikesRepository));

        return responseDtos;
    }

    //좋아요 등록/취소
    public String likeRecipe(Long postId, User user) {
        Recipe recipe = recipeRepository.findById(postId).orElseThrow(()->
                new CustomErrorException("해당 게시물이 존재하지 않아요"));
        //이미 좋아요누른 건지 확인하기
        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository.findByRecipeIdAndUserId(recipe.getId(),user.getId());
        if(foundRecipeLike.isPresent()){
            //이미 좋아요를 눌렀으면 좋아요취소
            recipeLikesRepository.delete(foundRecipeLike.get());
            return "좋아요 취소 성공";
        }else{
            RecipeLikes recipeLikes = new RecipeLikes(user, recipe);
            recipeLikesRepository.save(recipeLikes);
            return "좋아요 등록 성공";
        }

    }

    public Optional<Recipe> findById(Long recipeId) {
        return recipeRepository.findById(recipeId);
    }

    public Page<RecipeListResponseDto> searchByTag(String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        page = page-1;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Recipe> recipes = recipeRepository.findAllByTag(keyword, pageable);
//        System.out.println(recipes.getContent().get(0));
        Page<RecipeListResponseDto> responseDtos = recipes.map((recipe) -> new RecipeListResponseDto(recipe,userDetails, recipeLikesRepository));
        return responseDtos;
    }

    public Page<RecipeListResponseDto> searchByTitleOrContents(String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        page = page-1;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Recipe> recipes = recipeRepository.findAllByTitleOrContent(keyword, pageable);
        Page<RecipeListResponseDto> responseDtos = recipes.map((recipe) -> new RecipeListResponseDto(recipe,userDetails, recipeLikesRepository));
        return responseDtos;
    }

    //검색하기
    public Page<RecipeListResponseDto> searchRecipe(boolean withTag, String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {

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

        Page<Recipe> recipes = null;
        keyword = keyword.trim();
        if(withTag && !isSortByLikeCount) recipes = recipeRepository.findAllByTag(keyword, pageable);
        if(withTag && isSortByLikeCount) recipes = recipeRepository.findAllByTagOrderByLikeCount(keyword,pageable);
        if(!withTag && !isSortByLikeCount) recipes = recipeRepository.findAllByTitleOrContent(keyword, pageable);
        if(!withTag && isSortByLikeCount) recipes = recipeRepository.findAllByTitleOrContentOrderByLikeCount(keyword, pageable);
        Page<RecipeListResponseDto> responseDtos = recipes.map((recipe) -> new RecipeListResponseDto(recipe,userDetails, recipeLikesRepository));
        return responseDtos;
    }

    private void saveSearchAction(String keyword, User user) {
        RecipeSearchCount recipeSearchCount = new RecipeSearchCount(user, keyword);
        recipeSearchCountRepository.save(recipeSearchCount);
    }

    public List<RecipeListResponseDto> getPopularRecipe(String sortBy, User user) {
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

        List<Long> popularRecipeIdList = recipeRepository.findPopularRecipeId2(startDate, now);
        List<Optional<Recipe>> popularRecipeList = new ArrayList<>();
        popularRecipeIdList.forEach((recipeId)-> popularRecipeList.add(recipeRepository.findById(recipeId)));

        List<RecipeListResponseDto> responseDtoList = new ArrayList<>();
        popularRecipeList.forEach((recipe -> responseDtoList.add(new RecipeListResponseDto(recipe, user, recipeLikesRepository))));

        return responseDtoList;
    }

    public List<RecipeListResponseDto> getRecentRecipe(User user) {
        List<Recipe> popularRecipeIdList = recipeRepository.findTop3ByOrderByRegDateDesc();

        List<RecipeListResponseDto> responseDtoList = new ArrayList<>();
        popularRecipeIdList.forEach((recipe -> responseDtoList.add(new RecipeListResponseDto(recipe, user, recipeLikesRepository))));
        return responseDtoList;
    }

    public RecipeRecommendResponseDto getRecommendedRecipe(User user) {

        //0.현재 시간대 확인
        List<LocalDateTime> timeZone = getTimeZone();

        //1.해당 사용자의 기록이 존재하는지 체크
        System.out.println("시간확인:"+timeZone.get(0)+"//"+timeZone.get(1));
        List<Object[]> objectList= recipeRepository.checkUserHasData(user.getId(), timeZone.get(0),timeZone.get(1));
        boolean hasData =false;
        for(Object[] obj : objectList){
            if( (((BigInteger)obj[0]).intValue() >0 ) || (((BigInteger)obj[1]).intValue() >0 ) ||(((BigInteger)obj[2]).intValue() >0 ) ) hasData = true;
            System.out.println("되라:"+((BigInteger)obj[2]));
        }
        //2.존재하는 경우 태그와 레시피id 추출- 해당사용자 기록기반, 존재하지 않는 경우- 전체사용자 기록기반
        List<Object[]> foundRecipeAndTagName = (hasData)?
                recipeRepository.findRecommendedRecipeIdBasedOne(user.getId(),timeZone.get(0),timeZone.get(1))
                : recipeRepository.findRecommendedRecipeIdBasedAll(timeZone.get(0),timeZone.get(1));

        //1등으로 뽑힌 레시피id로 레시피 검색
        Long recipe_id = 1L;
        String tagName = "";
        for(Object[] obj : foundRecipeAndTagName){
            recipe_id = ((BigInteger)obj[0]).longValue();
//            tagName = (String)obj[1];
        }
//        Long recipe_id = ((BigInteger)foundRecipeAndTagName.get(0)[0]).longValue();
//        String tagName = (String)foundRecipeAndTagName.get(0)[1];
        Recipe recommendedRecipe = recipeRepository.findById(recipe_id).orElseThrow(()->new CustomErrorException("id로 해당 게시물 찾을 수 없음"));
        RecipeRecommendResponseDto responseDto = new RecipeRecommendResponseDto(recommendedRecipe, tagName, user, recipeLikesRepository);
        return responseDto;
    }

    private List<LocalDateTime> getTimeZone() {
        LocalDateTime morningStart = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(),
                4,0,0,0);
        LocalDateTime mornigEnd = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(),
                11,0,0,0);
        LocalDateTime lunchStart = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(),
                11,0,0,0);
        LocalDateTime lunchEnd = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(),
                15,0,0,0);
        LocalDateTime otherStart = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth(),
                15,0,0,0);
        LocalDateTime otherEnd = LocalDateTime.of(LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                LocalDateTime.now().getDayOfMonth()+1,
                4,0,0,0);

        LocalDateTime now = LocalDateTime.now();

        if(now.isAfter(morningStart) && now.isBefore(mornigEnd)) return Arrays.asList(morningStart,mornigEnd);
        else if(now.isAfter(lunchStart) && now.isBefore(lunchEnd))  return Arrays.asList(lunchStart,lunchEnd);
        else return Arrays.asList(otherStart,otherEnd);

    }
}
