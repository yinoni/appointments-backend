package com.example.appointments_app.model.user;

public class UserUpdateRequest {
    private String fullName;


    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
