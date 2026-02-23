package com.example.appointments_app.oauth2;

import com.example.appointments_app.jwt.JwtService;
import com.example.appointments_app.model.CustomUserDetails;
import com.example.appointments_app.model.User;
import com.example.appointments_app.model.UserIn;
import com.example.appointments_app.repo.UserRepository;
import com.example.appointments_app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;



    @Override
// שים לב: בלי @Transactional כאן כדי למנוע את ה-AssertionFailure
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email").toString().toLowerCase().trim();
        String fullName = oAuth2User.getAttribute("name");

        // 1. חיפוש ראשוני
        User finalUser = userRepository.findUserByEmail(email).orElse(null);

        // 2. אם לא נמצא, ננסה ליצור בבלוק נפרד
        if (finalUser == null) {
            try {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFullName(fullName);
                newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

                finalUser = userRepository.saveAndFlush(newUser);
            } catch (Exception e) {
                // אם ה-save נכשל (כי מישהו אחר הכניס בשבריר שנייה), נשלוף שוב
                finalUser = userRepository.findUserByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User conflict"));
            }
        }

        CustomUserDetails userDetails = new CustomUserDetails(finalUser.getId(), finalUser.getEmail(), finalUser.getPassword(), Collections.emptyList());

        String token = jwtService.generateToken(userDetails);
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/login-success?token=" + token);



    }
}
