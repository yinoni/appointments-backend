package com.example.appointments_app.model;

import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class ScheduleBuilder {
    private Long id;
    private LocalDate date;
    private Business business;
    private List<Appointment> appointments;
    private @Min(5) Integer min_duration;
    private LocalTime start_time;
    private LocalTime end_time;

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

    public ScheduleBuilder withMin_duration(Integer min_duration) {
        this.min_duration = min_duration;
        return this;
    }

    public ScheduleBuilder withStart_time(LocalTime start_time) {
        this.start_time = start_time;
        return this;
    }

    public ScheduleBuilder withEnd_time(LocalTime end_time) {
        this.end_time = end_time;
        return this;
    }

    public Schedule build() {
        Schedule schedule = new Schedule();
        schedule.setId(id);
        schedule.setDate(date);
        schedule.setBusiness(business);
        schedule.setAppointments(appointments);
        schedule.setMin_duration(min_duration);
        schedule.setStart_time(start_time);
        schedule.setEnd_time(end_time);
        return schedule;
    }
}
