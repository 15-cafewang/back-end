package com.sparta.backend.service.Recipe;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.Recipe.Recipe;
import com.sparta.backend.domain.Recipe.RecipeImage;
import com.sparta.backend.domain.Recipe.RecipeLikes;
import com.sparta.backend.domain.User;
import com.sparta.backend.dto.request.recipes.PostRecipeRequestDto;
import com.sparta.backend.dto.response.recipes.RecipeDetailResponsetDto;
import com.sparta.backend.dto.response.recipes.RecipeListResponseDto;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.RecipeImageRepository;
import com.sparta.backend.repository.RecipeLikesRepository;
import com.sparta.backend.repository.RecipeRepository;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    public RecipeService(
            RecipeRepository recipeRepository,
            RecipeLikesRepository recipeLikesRepository,
            AmazonS3Client amazonS3Client,
            S3Uploader s3Uploader,
            RecipeImageRepository recipeImageRepository
    ){
        this.recipeRepository = recipeRepository;
        this.recipeLikesRepository = recipeLikesRepository;
        this.amazonS3Client = amazonS3Client;
        this.s3Uploader = s3Uploader;
        this.recipeImageRepository = recipeImageRepository;
    }

    //레시피 저장
    public Recipe saveRecipe(PostRecipeRequestDto requestDto, User user) throws IOException {
        List<String> imageUrlList= uploadManyImagesToS3(requestDto, "recipeImage");
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
        if(requestDto.getImage1() != null) savedImages.add(s3Uploader.upload(requestDto.getImage1(),dirName));
        if(requestDto.getImage2() != null) savedImages.add(s3Uploader.upload(requestDto.getImage2(),dirName));
        if(requestDto.getImage3() != null) savedImages.add(s3Uploader.upload(requestDto.getImage3(),dirName));
        if(requestDto.getImage4() != null) savedImages.add(s3Uploader.upload(requestDto.getImage4(),dirName));
        if(requestDto.getImage5() != null) savedImages.add(s3Uploader.upload(requestDto.getImage5(),dirName));

        return savedImages;
    }
    //여러장의 이미지를 db에 저장하는 기능
    public Recipe uploadManyImagesToDB(List<String> imageUrlList, PostRecipeRequestDto requestDto, User user){
        Recipe recipe = new Recipe(requestDto.getTitle(),requestDto.getContent(),requestDto.getPrice(),user);
        //디비에 이미지url저장
        List<RecipeImage> recipeImages = new ArrayList<>();
        imageUrlList.forEach((image)-> recipeImages.add(new RecipeImage(image,recipe)));
        recipeImageRepository.saveAll(recipeImages);
        return recipe;
    }

    //레시피 수정
    //todo: 문제점: 중간에 익셉션 터져서 롤백한 상황이라면, S3에서 수정한건 롤백이 안된다.
    @Transactional
    public Recipe updateRecipe(Long recipeId,PostRecipeRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        //게시글 존재여부확인
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()->new CustomErrorException("해당 게시물을 찾을 수 없습니다"));

        //S3에 있는 사진 삭제
        for(int i=0; i<recipe.getRecipeImagesList().size();i++){
            RecipeImage recipeImage = recipe.getRecipeImagesList().get(i);
            if(recipeImage!= null) deleteS3(recipeImage.getImage());
        }
        //S3에 이미지 업로드
        List<String> imageUrlList= uploadManyImagesToS3(requestDto, "recipeImage");

        //DB의 recipe_image 기존 row들 삭제(그냥 update하면 더 작은 개수로 image업뎃할때 outOfInedex에러남)
        recipeImageRepository.deleteAllByRecipe(recipe);

        List<RecipeImage> recipeImageList = new ArrayList<>();
        imageUrlList.forEach((image)->recipeImageList.add(new RecipeImage(image,recipe)));
        recipeImageRepository.saveAll(recipeImageList);

        //이미지 외 다른 내용들 수정
        String title = requestDto.getTitle();
        String content = requestDto.getContent();
        int price = requestDto.getPrice();

        return recipe.updateRecipe(title,content,price);
    }

    //S3 이미지 삭제
    public void deleteS3(@RequestParam String imageName){
        //https://S3 버킷 URL/버킷에 생성한 폴더명/이미지이름
        String keyName = imageName.split("/")[4]; // 이미지이름만 추출

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
        String nickname = recipe.getUser().getNickname();
        String title = recipe.getTitle();
        String content = recipe.getContent();
        LocalDateTime regDate = recipe.getRegDate();
        int likeCount = recipe.getRecipeLikesList().size();
//        String image = recipe.getImage();

        Optional<RecipeLikes> foundRecipeLike = recipeLikesRepository.findByRecipeIdAndUserId(recipe.getId(),userDetails.getUser().getId());
        Boolean likeStatus = foundRecipeLike.isPresent();

        List<String> tagNames = new ArrayList<>();
        recipe.getTagList().forEach((tag)->tagNames.add(tag.getName()));

        List<String> images =new ArrayList<>();
        recipe.getRecipeImagesList().forEach((recipeImage)->images.add(recipeImage.getImage()));
        RecipeDetailResponsetDto responsetDto = new RecipeDetailResponsetDto(
                recipeId, nickname, title, content, regDate, likeCount, likeStatus, images, tagNames);

        return responsetDto;
    }

    public Page<RecipeListResponseDto> getRecipesByPage(int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Recipe> recipes = recipeRepository.findAll(pageable);

        Page<RecipeListResponseDto> responseDtos = recipes.map((recipe)->new RecipeListResponseDto(recipe, userDetails,recipeLikesRepository));

        return responseDtos;
    }

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

    public Page<RecipeListResponseDto> searchRecipe(boolean withTag, String keyword, int page, int size, boolean isAsc, String sortBy, UserDetailsImpl userDetails) {
        page = page-1;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        Page<Recipe> recipes;
        if(withTag){
            recipes = recipeRepository.findAllByTag(keyword, pageable);
        }else{
            recipes = recipeRepository.findAllByTitleOrContent(keyword, pageable);
        }
        Page<RecipeListResponseDto> responseDtos = recipes.map((recipe) -> new RecipeListResponseDto(recipe,userDetails, recipeLikesRepository));
        return responseDtos;
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
}
