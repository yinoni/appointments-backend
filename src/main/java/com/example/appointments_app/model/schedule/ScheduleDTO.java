package com.example.appointments_app.model.schedule;

import com.example.appointments_app.model.appointment.AppointmentDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleDTO {
    private Long id;
    private LocalDate date;
    private Long business;
    private Map<LocalTime, Boolean> hours;
    private List<AppointmentDTO> appointments = new ArrayList<>();

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

    public Map<LocalTime, Boolean> getHours() {
        return this.hours;
    }

    public void setHours(Map<LocalTime, Boolean> hours) {
        this.hours = hours;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }
}
