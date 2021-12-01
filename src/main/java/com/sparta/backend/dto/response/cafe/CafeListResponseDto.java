package com.sparta.backend.dto.response.cafe;

import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.cafe.Cafe;
import com.sparta.backend.domain.cafe.CafeLike;
import com.sparta.backend.domain.user.User;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.repository.cafe.CafeLikeRepository;
import com.sparta.backend.security.UserDetailsImpl;
import com.sparta.backend.util.NullThumbNailChecker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class CafeListResponseDto {
    private Long cafeId;
    private String nickname;
    private String title;
    private String content;
//    private List<String> images = new ArrayList<>();
    private String image;
    private LocalDateTime regDate;
    private int commentCount;
    private int likeCount;
    private String location;
    private boolean likeStatus;
    private int rankingStatus;


    public CafeListResponseDto(Cafe cafe, UserDetailsImpl userDetails, CafeLikeRepository cafeLikeRepository, S3Uploader s3Uploader) {
        NullThumbNailChecker nullThumbNailChecker = new NullThumbNailChecker(s3Uploader);
        try {
            nullThumbNailChecker.updateNullThumbNail(cafe);
        } catch (IOException e) {
            throw new CustomErrorException("섬네일 생성과정 실패");
        }

        this.cafeId = cafe.getId();
        this.nickname = cafe.getUser().getNickname();
        this.title = cafe.getTitle();
        this.content = cafe.getContent();
        this.regDate = cafe.getRegDate();
        this.commentCount = cafe.getCafeCommentList().size();
        this.image = getThumbNail(cafe);
        this.likeCount = cafe.getCafeLikeList().size();
        this.location = cafe.getLocation();

        Optional<CafeLike> foundCafeLike = cafeLikeRepository.findByCafeIdAndUserId(cafe.getId(),userDetails.getUser().getId());
        this.likeStatus = foundCafeLike.isPresent();
        rankingStatus = userDetails.getUser().getRankingStatus();
    }
//
    public CafeListResponseDto(Cafe cafe, User user, CafeLikeRepository cafeLikeRepository,S3Uploader s3Uploader){
        NullThumbNailChecker nullThumbNailChecker = new NullThumbNailChecker(s3Uploader);
        try {
            nullThumbNailChecker.updateNullThumbNail(cafe);
        } catch (IOException e) {
            throw new CustomErrorException("섬네일 생성과정 실패");
        }

        this.cafeId = cafe.getId();
        this.nickname = cafe.getUser().getNickname(); //todo:N+1 해결하면 될듯
        this.title = cafe.getTitle();
        this.content = cafe.getContent();
        this.regDate = cafe.getRegDate();
        this.commentCount = cafe.getCafeCommentList().size();
        this.image = getThumbNail(cafe);
        this.likeCount = cafe.getCafeLikeList().size();
        this.location = cafe.getLocation();

        Optional<CafeLike> foundCafeLike = cafeLikeRepository.findByCafeAndUser(cafe,user);
        this.likeStatus = foundCafeLike.isPresent();
        rankingStatus = user.getRankingStatus();
    }

    public CafeListResponseDto(Optional<Cafe> cafe, User user, CafeLikeRepository likesRepository, S3Uploader s3Uploader) {
        NullThumbNailChecker nullThumbNailChecker = new NullThumbNailChecker(s3Uploader);
        try {
            nullThumbNailChecker.updateNullThumbNail(cafe.get());
        } catch (IOException e) {
            throw new CustomErrorException("섬네일 생성과정 실패");
        }

        this.cafeId = cafe.get().getId();
        this.nickname = cafe.get().getUser().getNickname();
        this.title = cafe.get().getTitle();
        this.content = cafe.get().getContent();
        this.regDate = cafe.get().getRegDate();
        this.commentCount = cafe.get().getCafeCommentList().size();
        this.image = getThumbNail(cafe.get());
        this.likeCount = cafe.get().getCafeLikeList().size();
        this.location = cafe.get().getLocation();

        Optional<CafeLike> foundCafeLike = likesRepository.findByCafeIdAndUserId(cafe.get().getId(), user.getId());
        this.likeStatus = foundCafeLike.isPresent();
        rankingStatus = user.getRankingStatus();
    }

    public String getThumbNail(Cafe cafe) {
        if(cafe.getThumbNailImage() == null) return cafe.getCafeImagesList().get(0).getImage();
        else return cafe.getThumbNailImage();
    }

//    public String updateNullThumbNail(Cafe cafe, S3Uploader s3Uploader) throws IOException {
//        //섬네일이 있는지 체크
//        if(cafe.getThumbNailImage() != null) return "";
//
//        //섬네일이 없다면 CafeImage에 있는 사진 중 첫번째 사진 가져오기
//        String originalImageUrl = cafe.getCafeImagesList().get(0).getImage();
//
//        //가져온 사진을 리사이징
//        URL url = new URL(originalImageUrl);
//        Image image = ImageIO.read(url);
//        BufferedImage resizedImage = s3Uploader.resizeImage(image, 200, 200);
//
//        //리사이징한 이미지를 S3섬네일 폴더에 업로드
//        File imageFile = new File(originalImageUrl);
//        String savedThumbNailUrl = s3Uploader.uploadBufferedImageToS3(resizedImage, "thumbNail",imageFile);
//
//        //업로드한 이미지 URL을 디비에 저장
//        Cafe updatedCafe = cafe.updateCafeNullThumbNail(savedThumbNailUrl);
//
//        return savedThumbNailUrl;
//    }
}