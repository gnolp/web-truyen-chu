package com.javaweb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để phục vụ tài nguyên từ thư mục uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/F:/gt/Jav/test/src/main/resources/uploads/");
    }
}