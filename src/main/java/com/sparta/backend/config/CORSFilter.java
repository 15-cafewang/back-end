package com.sparta.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
//implements Filter
public class CORSFilter{
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    // CORS 설정
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse resp = (HttpServletResponse) servletResponse;
//
//        ((HttpServletResponse) servletResponse).setHeader("Access-Control-Allow-Origin", "https://mycipe.shop");
//        ((HttpServletResponse) servletResponse).setHeader("Access-Control-Allow-Credentials", "true");
//        ((HttpServletResponse) servletResponse).setHeader("Access-Control-Allow-Methods","POST, GET, PUT, DELETE, HEAD, OPTIONS");
//        ((HttpServletResponse) servletResponse).setHeader("Access-Control-Max-Age","3600");
//        ((HttpServletResponse) servletResponse).setHeader("Access-Control-Allow-Headers","Content-Type, Accept, X-Requested-With, remember-me");
//
//
////        if (request.getMethod().equals("OPTIONS")) {
////            resp.setStatus(HttpServletResponse.SC_OK);
////            return;
////        }
//        chain.doFilter(request, servletResponse);
//    }
//
//    @Override
//    public void destroy() {
//
//    }

}
