package com.example.appointments_app.controller;

import com.example.appointments_app.jwt.JwtService;
import com.example.appointments_app.model.authentication.*;
import com.example.appointments_app.model.user.SendOTPCodeRequest;
import com.example.appointments_app.model.user.User;
import com.example.appointments_app.model.user.UserIn;
import com.example.appointments_app.service.AuthService;
import com.example.appointments_app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/authenticate")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager, AuthService authService){
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletRequest httpReq){

        try{
            Authentication authentication =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = authService.generateRefreshToken(httpReq.getHeader("User-Agent"), user.getId());

            AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

            ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)          // קריטי! חוסם גישת JavaScript (הגנת XSS)
                    .secure(true)            // מאפשר שליחה רק מעל פרוטוקול HTTPS מוצפן
                    .path("/")               // תקף לכל הנתיבים באתר שלך
                    .maxAge(Duration.ofDays(30).toSeconds()) // אורך החיים של העוגייה (תואם לרדיס)
                    .sameSite("Strict")      // הגנה מפני התקפות CSRF (שליחת עוגיות מאתרים זרים)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString()) // מוסיף את העוגייה ל-Headers
                    .body(response); // ה-Refresh Token כבר לא פה! הוא בעוגייה
        }
        catch (Exception e){
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserIn userIn, HttpServletRequest request){
        User userRes = userService.register(userIn);
        CustomUserDetails userDetails = new CustomUserDetails(userRes.getId(), userRes.getEmail(), userRes.getPhoneNumber(), userRes.getPassword(), false, new ArrayList<>());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = authService.generateRefreshToken(request.getHeader("User-Agent"), userDetails.getId());

        AuthenticationResponse response = new AuthenticationResponse(accessToken, refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)          // קריטי! חוסם גישת JavaScript (הגנת XSS)
                .secure(true)            // מאפשר שליחה רק מעל פרוטוקול HTTPS מוצפן
                .path("/")               // תקף לכל הנתיבים באתר שלך
                .maxAge(Duration.ofDays(30).toSeconds()) // אורך החיים של העוגייה (תואם לרדיס)
                .sameSite("Strict")      // הגנה מפני התקפות CSRF (שליחת עוגיות מאתרים זרים)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // מוסיף את העוגייה ל-Headers
                .body(response); // ה-Refresh Token כבר לא פה! הוא בעוגייה
    }

    @PostMapping("/phone-verify")
    public ResponseEntity<?> phoneVerify(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody PhoneVerifyInput phoneVerifyInput){
        userService.verifyPhoneNumber(customUserDetails.getPhoneNumber(), phoneVerifyInput.getCode());
        customUserDetails.setVerified(true);

        String accessToken = jwtService.generateAccessToken(customUserDetails);

        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOTP(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        userService.resendOtpCode(customUserDetails.getPhoneNumber());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.resendOtpCode(userDetails.getPhoneNumber());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody(required = false)RefreshRequest refreshRequest, HttpServletRequest request){
        String refreshToken = null;

        if(request.getCookies() != null){
            refreshToken = Arrays.stream(request.getCookies())
                    .filter(c -> "refresh_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            System.out.println("The cookie is ===> " + refreshToken);
        }

        //GET TO REDIS TO CHECK IF THE REFRESH TOKEN EXISTS AND VALID
        refreshToken = refreshRequest.getRefreshToken();

        if(refreshToken != null) {
            String userAgent = request.getHeader("User-Agent");
            //GET TO REDIS TO CHECK IF THE REFRESH TOKEN EXISTS AND VALID
            Long userId = authService.validateRefreshToken(refreshToken, userAgent);

            User user = userService.findById(userId);
            CustomUserDetails userDetails = new CustomUserDetails(user.getId(), user.getEmail(), user.getPhoneNumber(), user.getPassword(), user.isVerified(), new ArrayList<>());

            String accessToken = jwtService.generateAccessToken(userDetails);

            return ResponseEntity.ok(accessToken);
        }

        return new ResponseEntity("Refresh token is invalid", HttpStatus.FORBIDDEN);
    }

}
