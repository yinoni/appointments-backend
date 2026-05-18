package com.example.appointments_app.model.authentication;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private String phoneNumber;
    private boolean verified;
    private Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(Long id, String email, String phoneNumber, String password, boolean verified, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.id = id;
        this.authorities = authorities;
        this.phoneNumber = phoneNumber;
        this.verified = verified;
    }


    public Long getId() {
        return id;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    // שאר המתודות של הממשק (חייב לממש את כולן)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
