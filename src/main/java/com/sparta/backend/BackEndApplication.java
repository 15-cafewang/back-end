package com.sparta.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class BackEndApplication {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
						.allowedOrigins("https://cafewang.co.kr")
//                        .allowedOrigins("http://localhost:3000")
//                        .allowedOrigins("*")
                        .maxAge(3000)
                        .allowedHeaders("header1", "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method"
                                , "Access-Control-Request-Headers", "Authorization")
                        .exposedHeaders("header1", "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method"
                                , "Access-Control-Request-Headers", "Authorization")
                        .allowedMethods("PUT", "DELETE", "GET", "HEAD", "PATCH", "POST");
            }
        };
    }

}
