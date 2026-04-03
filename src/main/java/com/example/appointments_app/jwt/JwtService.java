package com.example.appointments_app.jwt;

import com.example.appointments_app.model.authentication.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private static final long EXPIRATION =
            1000 * 60 * 60; // שעה

    public JwtService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }


    public String generateToken(CustomUserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .claim("id", userDetails.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }



    public boolean isTokenValid(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername());
    }

    public Date extractDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {

        String cleanToken = token;
        if (token != null && token.startsWith("Bearer ")) {
            cleanToken = token.substring(7).trim(); // חיתוך "Bearer " וניקוי רווחים בקצוות
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

        return resolver.apply(claims);
    }


}
