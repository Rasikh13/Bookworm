package com.bookworm.backend.config;

import com.bookworm.backend.security.UserOwnershipInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserOwnershipInterceptor userOwnershipInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userOwnershipInterceptor)
                .addPathPatterns("/api/v1/users/{userId}/**");
    }
}
