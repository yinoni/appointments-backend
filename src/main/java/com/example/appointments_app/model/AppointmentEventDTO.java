package com.example.appointments_app.model;

import java.io.Serializable;
import java.time.LocalTime;

public class AppointmentEventDTO implements Serializable {
    private String fullname;
    private String phone;
    private LocalTime time;
    private String businessName;


    public AppointmentEventDTO(String fullname, String phone, LocalTime time, String businessName) {
        this.fullname = fullname;
        this.phone = phone;
        this.time = time;
        this.businessName = businessName;
    }

    public AppointmentEventDTO() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
