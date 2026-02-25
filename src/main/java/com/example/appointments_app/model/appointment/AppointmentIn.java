package com.example.appointments_app.model.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

import static com.example.appointments_app.model.appointment.AppointmentBuilder.anAppointment;

public class AppointmentIn {
    private Long serviceId;
    @JsonFormat(pattern = "HH:mm")
    LocalTime time;
    private String fullName;
    private String phone;
    private Long scheduleId;

    public AppointmentIn() {
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
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

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Appointment toAppointment(){
        return anAppointment()
                .withStatus("ACCEPTED")
                .withTime(time)
                .build();
    }
}
