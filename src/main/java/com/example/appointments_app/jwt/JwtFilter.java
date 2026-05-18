package com.example.appointments_app.jwt;

import com.example.appointments_app.exception.UserNotVerified;
import com.example.appointments_app.redis.Redis;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.example.appointments_app.service.UserService.LOGGED_OUT_SET_REDIS_KEY;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final Set<String> ALLOWED_UNVERIFIED_PATHS = Set.of(
            "/authenticate/phone-verify",
            "/authenticate/send-otp",
            "/authenticate/resend-code"
            );

    @Autowired
    private Redis redis;

    public JwtFilter(JwtService jwtService,  UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Getting the Authorization header from the request
        String authHeader = request.getHeader("Authorization");
        String requestPath = request.getRequestURI();

        //Checking if the header value starts with 'Bearer'
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);


            //If the username is not null, and if there is an authenticated user already
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails user =
                        userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, user)) {

                    //Checking if the JWT is in te "blacklist" in redis
                    if(redis.isInSet(LOGGED_OUT_SET_REDIS_KEY, authHeader))
                        throw new AuthenticationException("Invalid token");

                    boolean verified = jwtService.extractVerified(token);
                    if(!verified){
                        if(!ALLOWED_UNVERIFIED_PATHS.contains(requestPath)){
                            // 1. הגדרת הסטטוס ל-403 Forbidden
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            // 2. כתיבת גוף השגיאה בפורמט JSON (כדי שהקclient ידע למה הוא נחסם)
                            String jsonResponse = "{"
                                    + "\"success\": false,"
                                    + "\"msg\": \"User is not verified \","
                                    + "\"code\": \"USER_NOT_VERIFIED\","
                                    + "\"statusCode\": 403"
                                    + "}";

                            response.getWriter().write(jsonResponse);

                            // 3. קריטי: עוצרים את שרשרת הפילטרים ולא קוראים ל-filterChain.doFilter
                            return;
                        }

                    }

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());

                    SecurityContextHolder.getContext()
                            .setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
