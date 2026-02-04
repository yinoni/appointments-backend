package com.example.appointments_app.redis;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class Redis {
    private final RedisTemplate redisTemplate;

    public Redis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean setBit(String key, Integer bit, boolean value){
        return redisTemplate.opsForValue().setBit(key, bit, value);
    }

    public boolean getBit(String key, Integer bit){
        return  redisTemplate.opsForValue().getBit(key, bit);
    }

    public void setOffsetsPipelined(String key, List<LocalTime> hours, int day_divide) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (LocalTime hour : hours) {
                long minutesFromMidnight = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, hour);
                long offset = minutesFromMidnight / day_divide;

                // שים לב: כאן משתמשים ב-connection הישיר של Redis
                connection.stringCommands().setBit(key.getBytes(), offset, true);
            }
            return null; // ה-Template אוסף את התשובות לבד
        });
    }
}
