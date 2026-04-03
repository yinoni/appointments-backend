package com.example.appointments_app.redis;

import org.springframework.cglib.core.Local;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class Redis {
    private final RedisTemplate redisTemplate;

    private final int DAY_DIVIDER = 5;
    public static final String OTP_PREFIX = "user:otp:";
    private final int OTP_DURATION = 60;

    public Redis(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setKey(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public void setKey(String key, String value, int exp, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value, exp, timeUnit);
    }

    public Object getKey(String key){
        return redisTemplate.opsForValue().get(key);
    }


    public boolean deleteKey(String key){
        return redisTemplate.delete(key);
    }

    public boolean setBit(String key, Long offset, boolean value){
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public boolean getBit(String key, Integer bit){
        return  redisTemplate.opsForValue().getBit(key, bit);
    }

    public void setOffsetsPipelined(String key, LocalTime start, LocalTime end) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (long i = getOffset(start); i <= getOffset(end); i++) {
                // שים לב: כאן משתמשים ב-connection הישיר של Redis
                connection.stringCommands().setBit(key.getBytes(), i, false);
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

    public Map<LocalTime, Boolean> getHoursFromOffsetRange(String key, long start, long end, int min_duration){
        List<LocalTime> availableHours = new ArrayList<>();
        Map<LocalTime, Boolean> hoursMap = new HashMap<>();

        for(long i = start; i<=end; i+=(min_duration / DAY_DIVIDER)){
            LocalTime temp = offsetToLocalTime(min_duration, i);
            boolean free = false;
            if(!redisTemplate.opsForValue().getBit(key, i )){
                availableHours.add(temp);
                free = true;
            }

            hoursMap.put(temp, free);
        }

        return hoursMap;
    }

    /***
     *
     * @param phone - The user phone number
     * @param privateCode - The 4-digit code that has been sent to him
     */
    public void saveOtp(String phone, String privateCode){
        String key = OTP_PREFIX + phone;
        this.setKey(key, privateCode, OTP_DURATION, TimeUnit.SECONDS);
        this.setKey(OTP_PREFIX + phone + ":counter", "0", OTP_DURATION, TimeUnit.SECONDS);
    }

    /***
     *
     * @param phone - The user phone number
     * @return - This function increase the attempts counter by one and returns it
     */
    public int incrementAndGetCounter(String phone) {
        String counterKey = OTP_PREFIX + phone + ":counter";
        Long val = redisTemplate.opsForValue().increment(counterKey);
        return val != null ? val.intValue() : 0;
    }

    /***
     *
     * @param phone - The user phone number
     * @return - This function returns the OTP code that sent to this phone number
     */
    public String getOtpCode(String phone) {
        Object code = redisTemplate.opsForValue().get(OTP_PREFIX + phone);
        return code != null ? code.toString() : null;
    }

    public void toggleBit(String key, LocalTime hour) {
        long offset = getOffset(hour);
        BitFieldSubCommands.BitFieldType u1 = BitFieldSubCommands.BitFieldType.unsigned(1);

        // יצירת הפקודה: Increment ב-1 במיקום ה-offset
        BitFieldSubCommands subCommands = BitFieldSubCommands.create()
                .incr(u1)
                .valueAt(offset) // בחלק מהגרסאות זה 'at', באחרות 'valueAt'
                .by(1);

        // הרצה - מחזיר רשימה של הערכים החדשים לאחר הפעולה
        List<Long> results = redisTemplate.opsForValue().bitField(key, subCommands);
    }

    public void addToSet(String key, String value){
        redisTemplate.opsForSet().add(key, value);
    }

    public void addToSet(String key, String value, long ttl){
        redisTemplate.opsForSet().add(key, value, ttl);
    }

    public void removeFromSet(String key, String value){
        redisTemplate.opsForSet().remove(key, value);
    }

    public boolean isInSet(String key, String value){
        Boolean isMember = redisTemplate.opsForSet().isMember(key, value);

        return Boolean.TRUE.equals(isMember);
    }


}
