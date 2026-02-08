package com.example.appointments_app.redis;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class Redis {
    private final RedisTemplate redisTemplate;

    public Redis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean setBit(String key, Long offset, boolean value){
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public boolean getBit(String key, Integer bit){
        return  redisTemplate.opsForValue().getBit(key, bit);
    }

    public void setOffsetsPipelined(String key, List<LocalTime> hours, int day_divide) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (LocalTime hour : hours) {
                long offset = getOffset(day_divide, hour);

                // שים לב: כאן משתמשים ב-connection הישיר של Redis
                connection.stringCommands().setBit(key.getBytes(), offset, false);
            }
            return null; // ה-Template אוסף את התשובות לבד
        });
    }

    /***
     *
     * @param key - The key as string
     * @param hour - The hour that we need to check if it is taken or not
     * @param dayDivide - The divider of the day
     * @return - True if the hour is taken and false if it available

    public boolean tryToLockSlot(String key, LocalTime hour, int dayDivide) {
        long offset = getOffset(dayDivide, hour);

        Boolean wasAlreadyTaken = redisTemplate.opsForValue().setBit(key, offset, true);

        return Boolean.TRUE.equals(wasAlreadyTaken);
    }
    ]*/

    /***
     *
     * @param key - The key as string
     * @param hour - The hour that we need to check if it is taken or not
     * @param day_divide - The divider of the day
     * @param duration
     * @return - True if the hours is taken and false if all the hours slots are available
     */
    public boolean tryToLockSlot(String key, LocalTime hour, int day_divide, int duration){
        long startOffset = getOffset(day_divide, hour);
        int runs = duration / day_divide;

        String luaScript =
                "for i=0, ARGV[2]-1 do " +
                "  if redis.call('GETBIT', KEYS[1], ARGV[1] + i) == 1 then " +
                "    return 0 " + // תפוס, אל תעשה כלום
                "  end " +
                "end " +
                "for i=0, ARGV[2]-1 do " +
                "  redis.call('SETBIT', KEYS[1], ARGV[1] + i, 1) " +
                "end " +
                "return 1"; // הכל היה פנוי ותפסנו הכל

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(luaScript, Long.class);

        // הרצת הסקריפט בפעימה אחת לרדיס
        Long result = (Long) redisTemplate.execute(script,
                Collections.singletonList(key),
                startOffset, runs);

        return result != null && result == 1;
    }


    /***
     *
     * @param day_divide - The divider of the day (By minutes)
     * @param hour - The hour
     * @return - The offset of the hour in the redis db
     */
    public Long getOffset(int day_divide, LocalTime hour){
        long minutesFromMidnight = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, hour);
        return minutesFromMidnight / day_divide;
    }

}
