package com.example.appointments_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthManagerConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder encoder;

    public AuthManagerConfig(UserDetailsService uds,
                             PasswordEncoder encoder) {
        this.userDetailsService = uds;
        this.encoder = encoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
