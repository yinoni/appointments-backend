package com.example.appointments_app.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScheduleDTO {
    private Long id;
    private LocalDate date;
    private Long business;
    private List<AppointmentDTO> appointments;
    private List<LocalTime> available_hours;

    public ScheduleDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getBusiness() {
        return business;
    }

    public void setBusiness(Long business) {
        this.business = business;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public List<LocalTime> getAvailable_hours() {
        return available_hours;
    }

    public void setAvailable_hours(List<LocalTime> available_hours) {
        this.available_hours = available_hours;
    }
}
