package com.example.appointments_app.model;

import java.time.LocalTime;

public final class AppointmentBuilder {
    private Long id;
    private LocalTime time;
    private Service service;
    private User user;
    private String status;
    private Schedule schedule;

    private AppointmentBuilder() {
    }

    public static AppointmentBuilder anAppointment() {
        return new AppointmentBuilder();
    }

    public AppointmentBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public AppointmentBuilder withTime(LocalTime time) {
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

    public AppointmentBuilder withSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public Appointment build() {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setTime(time);
        appointment.setService(service);
        appointment.setUser(user);
        appointment.setStatus(status);
        appointment.setSchedule(schedule);
        return appointment;
    }
}
