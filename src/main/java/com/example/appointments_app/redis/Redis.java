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

    private final int DAY_DIVIDER = 5;

    public Redis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean setBit(String key, Long offset, boolean value){
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public boolean getBit(String key, Integer bit){
        return  redisTemplate.opsForValue().getBit(key, bit);
    }

    public void setOffsetsPipelined(String key, List<LocalTime> hours) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (LocalTime hour : hours) {
                long offset = getOffset(hour);

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
     * @param duration
     * @return - It tries to set the hours as taken and if the hours is already taken it returns false
     */
    public boolean tryToLockSlot(String key, LocalTime hour, int duration){
        long startOffset = getOffset(hour);
        int runs = duration / DAY_DIVIDER;

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
     * @param hour - The hour
     * @return - The offset of the hour in the redis db
     */
    public Long getOffset( LocalTime hour){
        long minutesFromMidnight = ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, hour);
        return minutesFromMidnight / DAY_DIVIDER;
    }

    public LocalTime offsetToLocalTime(int min_duration, long offset){
        int hour = Math.toIntExact((offset * DAY_DIVIDER) / 60);
        int minutes = Math.toIntExact((offset * DAY_DIVIDER) % 60);

        return LocalTime.of(hour, minutes);
    }

    public List<LocalTime> getHoursFromOffsetRange(String key, long start, long end, int min_duration){
        List<LocalTime> availableHours = new ArrayList<>();

        for(long i = start; i<=end; i++){
            long offset = (min_duration / DAY_DIVIDER) + i;
            if(!redisTemplate.opsForValue().getBit(key, offset ))
                availableHours.add(offsetToLocalTime(min_duration, offset));
        }

        return availableHours;
    }



}
