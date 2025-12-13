package com.sleekydz86.service.commu.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class RequestSizeConfig {

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> requestSizeFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          FilterChain filterChain) 
                    throws ServletException, IOException {
                long contentLength = request.getContentLengthLong();
                if (contentLength > 10 * 1024 * 1024) {
                    response.setStatus(413);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"resultCode\":\"4131\",\"resultMessage\":\"요청 크기가 10MB를 초과합니다\",\"resultData\":null}");
                    return;
                }
                filterChain.doFilter(request, response);
            }
        });
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}

