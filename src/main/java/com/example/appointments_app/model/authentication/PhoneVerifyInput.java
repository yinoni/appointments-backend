package com.example.appointments_app.model.authentication;

import com.example.appointments_app.model.user.UserIn;

public class PhoneVerifyInput {
    private String phoneNumber;
    private String code;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
