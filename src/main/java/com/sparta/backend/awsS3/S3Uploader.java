package com.sparta.backend.awsS3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.sparta.backend.exception.CustomErrorException;
import com.sparta.backend.exception.ImageNameTooLongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.*;
import java.net.URL;
import java.util.Base64;
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
    public BufferedImage resizeImage(File originalImage) throws IOException {
        int orientation = getOrientation(originalImage);
        BufferedImage in = rotateImage(originalImage, orientation);

        //imageLength[0] : 가로, imageLength[1] : 세로
        int[] imageLength = setImageRatio(in);

        BufferedImage resizedImage =
                new BufferedImage(imageLength[0], imageLength[1], BufferedImage.TYPE_3BYTE_BGR); // 썸네일이미지
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(in, 0, 0, imageLength[0], imageLength[1], null);
        graphics2D.dispose();
        return resizedImage;
    }

    //비율 유지하면서 썸네일 만들기
    public BufferedImage resizeImage(String originalImageUrl) throws IOException {
        Optional<File> optionalFile = convert(originalImageUrl);
        if(optionalFile.isEmpty()) {
            throw new CustomErrorException("섬네일 생성과정 실패");
        }
        File convertFile = optionalFile.get();
        int orientation = getOrientation(convertFile);
        BufferedImage in = rotateImage(convertFile, orientation);

        //imageLength[0] : 가로, imageLength[1] : 세로
        int[] imageLength = setImageRatio(in);

        BufferedImage tImage = new BufferedImage(imageLength[0], imageLength[1], BufferedImage.TYPE_3BYTE_BGR); // 썸네일이미지
        Graphics2D graphic = tImage.createGraphics();
        Image image = in.getScaledInstance(imageLength[0], imageLength[1], Image.SCALE_SMOOTH);
        graphic.drawImage(image, 0, 0, imageLength[0], imageLength[1], null);
        graphic.dispose(); // 리소스를 모두 해제;

        return tImage;
    }


    //썸네일 축소 비율 정하기
    //imageLength[0] : 가로, imageLength[1] : 세로
    public int[] setImageRatio(BufferedImage bufferedImage) {
        int[] imageLength = new int[2];

        double ratio; // 이미지 축소 비율

        int getWidth = bufferedImage.getWidth();
        int getHeight = bufferedImage.getHeight();

        if(getWidth < 250) ratio = 1;
        else if(getWidth < 700) ratio = 2;
        else if(getWidth < 1400) ratio = 5;
        else ratio = 10;

        int tWidth = (int) (getWidth / ratio); // 생성할 썸네일이미지의 너비
        int tHeight = (int) (getHeight / ratio); // 생성할 썸네일이미지의 높이

        imageLength[0] = tWidth; imageLength[1] = tHeight;

        return imageLength;
    }

    //이미지 회전 현상 방지
    public int getOrientation(File imageFile) throws IOException {
        int orientation = 1;    //사진 각도
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            try {
                if(directory != null){
                    orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION); // 회전정보
                }
            } catch (MetadataException me) {
                System.out.println("Could not get orientation" );
                orientation = 1;
                //removeNewFile(uploadedFile);
            }
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    public BufferedImage rotateImage(File imageFile, int orientation) throws IOException {
        BufferedImage srcImg = ImageIO.read(imageFile);
        // 회전 시킨다.
        switch (orientation) {
            case 6:
                srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_90, (BufferedImageOp) null);
                break;
            case 1:
                break;
            case 3:
                srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_180, (BufferedImageOp) null);
                break;
            case 8:
                srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_270, (BufferedImageOp) null);
                break;
            default:
                break;
        }
        return srcImg;
    }

    //resize한 후 s3에 업로드하기
    public String resizeAndUpload(MultipartFile multipartFile, String dirName) throws IOException {
        //dirName길이 제한.
        if(Objects.requireNonNull(multipartFile.getOriginalFilename()).length() >300 ) throw new ImageNameTooLongException("사진 이름이 너무 깁니다.");

        File uploadedFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new CustomErrorException("error: MultipartFile -> File convert fail")); //반환된 uploadFile은 로컬에 있는 사진위치임

        //파일 resize하기
        BufferedImage resizedImage = resizeImage(uploadedFile);
        return uploadBufferedImageToS3(resizedImage, dirName,uploadedFile);
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = getConvertFile(file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    // 로컬에 이미지 URL 파일 업로드 하기
    private Optional<File> convert(String originalImageUrl) throws IOException {
        URL url = new URL(originalImageUrl);
        BufferedImage bufferedImage = ImageIO.read(url);
        String fileName = originalImageUrl.substring(originalImageUrl.lastIndexOf("/") + 1);

        File convertFile = getConvertFile(fileName);
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", bos);
            try (
                    FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                    fos.write(bos.toByteArray());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    public File getConvertFile(String fileName) {
//        File convertFile = new File( "/home/ubuntu/images"+ "/" + fileName); // EC2용
        File convertFile = new File( "E:\\workspaces\\hanghae99\\06.mycipe\\imageupload"+ "\\" + fileName); // 로컬용

        return convertFile;
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
