package com.example.appointments_app.model.user;

import com.example.appointments_app.model.business.Business;

import java.util.Set;

public final class UserBuilder {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private String role;
    private Set<Business> businesses;

    private UserBuilder() {
    }

    public static UserBuilder anUser() {
        return new UserBuilder();
    }

    public UserBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withRole(String role) {
        this.role = role;
        return this;
    }

    public UserBuilder withBusinesses(Set<Business> businesses) {
        this.businesses = businesses;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setBusinesses(businesses);
        return user;
    }
}
