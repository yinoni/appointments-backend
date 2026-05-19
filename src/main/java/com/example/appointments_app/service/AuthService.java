package com.example.appointments_app.service;

import com.example.appointments_app.exception.AuthenticationException;
import com.example.appointments_app.model.authentication.AuthRequest;
import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.redis.Redis;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final Redis redis;
    private final String REFRESH_TOKEN_REDIS_KEY = "refresh_token:";

    public AuthService(Redis redis){
        this.redis = redis;
    }

    public String login(AuthRequest request){
        if(!request.getEmail().equals("y@dev.com") && request.getPassword().equals("12345"))
            throw new AuthenticationException("Username or password incorrect!", HttpStatus.BAD_REQUEST, "usernameOrPassword");

        return "logged in as " + request.getEmail();
    }

    public String generateRefreshToken(String userAgent, Long userId){
        String refreshToken = UUID.randomUUID().toString();

        redis.setKey(REFRESH_TOKEN_REDIS_KEY + refreshToken, userAgent + "||" + userId, 30, TimeUnit.DAYS);

        return refreshToken;
    }

    public Long validateRefreshToken(String refreshToken, String userAgent){
        Object redisValue = redis.getKey(REFRESH_TOKEN_REDIS_KEY + refreshToken);

        if(redisValue != null){
            String[] splitValue = redisValue.toString().split("\\|\\|");

            if (splitValue.length < 2)
                return null;

            String savedUserAgent = splitValue[0];
            Long savedUserId = Long.parseLong(splitValue[1]);

            if (Objects.equals(userAgent, savedUserAgent)) {
                return savedUserId; // הכל תקין! מחזירים את ה-ID של המשתמש בשביל ה-Access Token
            }
        }

        redis.deleteKey(REFRESH_TOKEN_REDIS_KEY + refreshToken);
        return null;
    }
}