package com.sleekydz86.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/modules/**")
                .addResourceLocations("/modules/");
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/js/");
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("/images/");
        
        registry.addResourceHandler("/css/**")
                .addResourceLocations("/css/");
        
        registry.addResourceHandler("/datepicker/**")
                .addResourceLocations("/datepicker/");
    }
}

