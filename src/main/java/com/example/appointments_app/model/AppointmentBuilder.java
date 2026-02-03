package com.example.appointments_app.model;

import java.time.LocalDateTime;

public final class AppointmentBuilder {
    private Long id;
    private LocalDateTime time;
    private Service service;
    private User user;
    private String status;

    private AppointmentBuilder() {
    }

    public static AppointmentBuilder anAppointment() {
        return new AppointmentBuilder();
    }

    public AppointmentBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AppointmentBuilder withTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public AppointmentBuilder withService(Service service) {
        this.service = service;
        return this;
    }

    public AppointmentBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public AppointmentBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public Appointment build() {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setTime(time);
        appointment.setService(service);
        appointment.setUser(user);
        appointment.setStatus(status);
        return appointment;
    }
}
