package com.sparta.backend.util;

import com.sparta.backend.awsS3.S3Uploader;
import com.sparta.backend.domain.cafe.Cafe;
import lombok.RequiredArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@RequiredArgsConstructor
public class NullThumbNailChecker {

    private final S3Uploader s3Uploader;

    public String updateNullThumbNail(Cafe cafe) throws IOException {
        //섬네일이 있는지 체크
        if(cafe.getThumbNailImage() != null) return "";

        //섬네일이 없다면 CafeImage에 있는 사진 중 첫번째 사진 가져오기
        String originalImageUrl = cafe.getCafeImagesList().get(0).getImage();

        //가져온 사진을 리사이징
        URL url = new URL(originalImageUrl);
        Image image = ImageIO.read(url);
        BufferedImage resizedImage = s3Uploader.resizeImage(image, 200, 200);

        //리사이징한 이미지를 S3섬네일 폴더에 업로드
        File imageFile = new File(originalImageUrl);
        String savedThumbNailUrl = s3Uploader.uploadBufferedImageToS3(resizedImage, "thumbNail",imageFile);

        //업로드한 이미지 URL을 디비에 저장
        Cafe updatedCafe = cafe.updateCafeNullThumbNail(savedThumbNailUrl);

        return savedThumbNailUrl;
    }
}
