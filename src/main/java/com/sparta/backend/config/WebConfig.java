package com.sparta.backend.config;

import com.sparta.backend.domain.constant.EnumMapper;
import com.sparta.backend.domain.constant.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order(1)
@Configuration
public class WebConfig {

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        enumMapper.put("UserRole", UserRole.class);

        return enumMapper;
    }
}
