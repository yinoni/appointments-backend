package com.example.appointments_app.model;

import static com.example.appointments_app.model.UserBuilder.anUser;

public class UserIn {

    private String fullName;
    private String phone;
    private String email;
    private String password;

    public UserIn() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toUser(){
        return anUser()
                .withFullName(this.fullName)
                .withPhoneNumber(this.phone)
                .withEmail(this.email)
                .withPassword(this.password)
                .build();
    }
}
