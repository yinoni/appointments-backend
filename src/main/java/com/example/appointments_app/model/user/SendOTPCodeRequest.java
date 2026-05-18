package com.example.appointments_app.model.user;

public class SendOTPCodeRequest {
    private String phoneNumber;

    public SendOTPCodeRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
