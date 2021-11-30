package com.sparta.backend.awsS3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.exception.ImageNameTooLongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    private final String bucket = "99final";  // S3 버킷 이름

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        //dirName길이 제한.
//        System.out.println("파일네임: "+multipartFile.getName());
//        System.out.println("오리지날파일네임: "+multipartFile.getOriginalFilename());
        if(Objects.requireNonNull(multipartFile.getOriginalFilename()).length() >300 ) throw new ImageNameTooLongException("사진 이름이 너무 깁니다.");

        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new CustomErrorException("error: MultipartFile -> File convert fail")); //반환된 uploadFile은 로컬에 있는 사진위치임

        return uploadToS3(uploadFile, dirName);
    }

    //섬네일 resize하기
    public BufferedImage resizeImage(File originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage in = ImageIO.read(originalImage);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(in, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public BufferedImage resizeImage(Image originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }


    //resize한 후 s3에 업로드하기
    public String resizeAndUpload(MultipartFile multipartFile, String dirName) throws IOException {
        //dirName길이 제한.
        if(Objects.requireNonNull(multipartFile.getOriginalFilename()).length() >300 ) throw new ImageNameTooLongException("사진 이름이 너무 깁니다.");

        File uploadedFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new CustomErrorException("error: MultipartFile -> File convert fail")); //반환된 uploadFile은 로컬에 있는 사진위치임

        //파일 resize하기
        BufferedImage resizedImage = resizeImage(uploadedFile, 200, 200);
        return uploadBufferedImageToS3(resizedImage, dirName,uploadedFile);
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
//        File convertFile = new File( "/home/ubuntu/images"+ "/" + file.getOriginalFilename()); // EC2용
        File convertFile = new File( "D:\\14_HangHae99\\Teamplay\\hh99-finalProject\\imageupload"+ "\\" + file.getOriginalFilename()); // 로컬용
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    //BufferedImage를 S3로 업로드하기
    public String uploadBufferedImageToS3(BufferedImage resizedImage, String dirName, File uploadedFile) throws IOException {

        //BufferedImage -> InputStream으로 변환
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage,"png",os);
        byte[] buffer = os.toByteArray();
        InputStream is = new ByteArrayInputStream(buffer);
        String fileName = dirName + "/" + UUID.randomUUID() + uploadedFile.getName();   // S3에 저장된 파일 이름
        ObjectMetadata meta = new ObjectMetadata();

        //s3에 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket,fileName,is,meta).withCannedAcl(CannedAccessControlList.PublicRead));

        //로컬(또는 우분투) 파일 삭제
        removeNewFile(uploadedFile);
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // S3로 파일 업로드하기
    private String uploadToS3(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }


}
