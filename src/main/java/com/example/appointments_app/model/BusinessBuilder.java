package com.example.appointments_app.model;

import java.util.List;

public final class BusinessBuilder {
    private Long id;
    private String businessName;
    private User owner;
    private Integer totalAppointments;
    private List<Service> services;
    private List<Schedule> schedules;
    private String description;
    private String city;
    private String street;

    private BusinessBuilder() {
    }

    public static BusinessBuilder aBusiness() {
        return new BusinessBuilder();
    }

    public BusinessBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BusinessBuilder withBusinessName(String businessName) {
        this.businessName = businessName;
        return this;
    }

    public BusinessBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public BusinessBuilder withTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
        return this;
    }

    public BusinessBuilder withServices(List<Service> services) {
        this.services = services;
        return this;
    }

    public BusinessBuilder withSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        return this;
    }

    public BusinessBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public BusinessBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public BusinessBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public Business build() {
        Business business = new Business();
        business.setId(id);
        business.setBusinessName(businessName);
        business.setOwner(owner);
        business.setTotalAppointments(totalAppointments);
        business.setServices(services);
        business.setSchedules(schedules);
        business.setDescription(description);
        business.setCity(city);
        business.setStreet(street);
        return business;
    }
}
