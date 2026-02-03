package com.example.appointments_app.model;

import java.time.LocalDate;
import java.util.List;

public final class ScheduleBuilder {
    private Long id;
    private LocalDate date;
    private Business business;
    private List<Appointment> appointments;

    private ScheduleBuilder() {
    }

    public static ScheduleBuilder aSchedule() {
        return new ScheduleBuilder();
    }

    public ScheduleBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ScheduleBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public ScheduleBuilder withBusiness(Business business) {
        this.business = business;
        return this;
    }

    public ScheduleBuilder withAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        return this;
    }

    public Schedule build() {
        Schedule schedule = new Schedule();
        schedule.setId(id);
        schedule.setDate(date);
        schedule.setBusiness(business);
        schedule.setAppointments(appointments);
        return schedule;
    }
}
