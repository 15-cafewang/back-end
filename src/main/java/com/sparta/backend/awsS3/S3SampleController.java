package com.sparta.backend.awsS3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class S3SampleController {
//이미지가 잘 업로드 되는지 테스트하기 위한 컨트롤러입니다.
    private final S3Uploader s3Uploader;

    @PostMapping("/image")
    public String upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "image");
    }

}
