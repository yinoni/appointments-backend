package com.example.appointments_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // המפתח (Key) תמיד יהיה String
        template.setKeySerializer(new StringRedisSerializer());

        // הערך (Value) עבור Bitmaps חייב להיות Serializer שלא משנה את הבייטים.
        // GenericToStringSerializer או StringRedisSerializer יכולים לעבוד,
        // אבל הכי בטוח לעבודה עם ביטים זה להשתמש ב-GenericToStringSerializer עבור מספרים
        // או פשוט להשאיר את ה-ValueSerializer כברירת מחדל (JdkSerialization) או ByteArray.
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));

        return template;
    }

}

