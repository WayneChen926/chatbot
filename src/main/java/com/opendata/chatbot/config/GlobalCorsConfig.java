package com.opendata.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // 解決 誇網域資源 cors 的問題
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //映射路徑
                registry.addMapping("/**")
                        //允許使用那些請求方式
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        //允許哪些Header
                        .allowedHeaders("*")
                        //允許跨網域請求的來源
                        .allowedOrigins("*");
            }
        };
    }
}
