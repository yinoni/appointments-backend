package com.example.appointments_app.jwt;

import com.example.appointments_app.model.authentication.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey accessSecretKey;

    private static final long ACCESS_EXPIRATION = TimeUnit.MINUTES.toMillis(60);

    public JwtService(SecretKey secretKey) {
        this.accessSecretKey = secretKey;
    }

    public String generateAccessToken(CustomUserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .claim("id", userDetails.getId())
                .claim("verified", userDetails.isVerified())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(accessSecretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }



    public boolean isTokenValid(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername()) && !isTokenExpired(token);
    }

    public Date extractDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractDate(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean extractVerified(String token){
        return extractClaim(token, claims -> claims.get("verified", Boolean.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {

        String cleanToken = token;
        if (token != null && token.startsWith("Bearer ")) {
            cleanToken = token.substring(7).trim(); // חיתוך "Bearer " וניקוי רווחים בקצוות
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(cleanToken)
                .getBody();

        return resolver.apply(claims);
    }


}
