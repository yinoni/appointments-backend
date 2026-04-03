package com.example.appointments_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // תקף לכל הניתובים
                        .allowedOrigins("http://localhost:8081") // הכתובת של ה-Frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // מתודות מותרות
                        .allowedHeaders("*") // הרשאת כל ה-Headers
                        .allowCredentials(true); // אם אתה משתמש ב-Cookies או Sessions
            }
        };
    }
}