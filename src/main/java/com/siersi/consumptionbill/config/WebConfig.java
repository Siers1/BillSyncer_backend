package com.siersi.consumptionbill.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 实现WebMvcConfigurer接口，用于配置Spring MVC相关设置
 * 主要负责配置拦截器，设置JWT身份验证拦截器的拦截规则
 * 
 * @author siersi
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    /**
     * 添加拦截器配置
     * 配置JWT拦截器对所有路径生效，但排除认证相关的路径
     * 
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(jwtInterceptor)        // 添加JWT拦截器
                .addPathPatterns("/**")                // 拦截所有路径
                .excludePathPatterns("/auth/**", "/websocket/**");      // 排除认证相关路径和WebSocket路径
    }
}
