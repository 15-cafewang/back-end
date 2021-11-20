package com.sparta.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
//                .allowedOrigins("http://mycipe.shop.s3-website.ap-northeast-2.amazonaws.com")
                .allowedMethods("POST", "GET", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowCredentials(true);
    }

}
