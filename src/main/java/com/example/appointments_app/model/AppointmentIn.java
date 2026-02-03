package com.example.appointments_app.model;

import static com.example.appointments_app.model.AppointmentBuilder.anAppointment;

public class AppointmentIn {
    private Long serviceId;
    private String time;
    private String fullName;
    private String phone;

    public AppointmentIn() {
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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


}
