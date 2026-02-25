package com.example.appointments_app.model.authentication;

public final class AuthRequestBuilder {
    private String email;
    private String password;

    private AuthRequestBuilder() {
    }

    public static AuthRequestBuilder anAuthRequest() {
        return new AuthRequestBuilder();
    }

    public AuthRequestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public AuthRequestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public AuthRequest build() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(email);
        authRequest.setPassword(password);
        return authRequest;
    }
}
