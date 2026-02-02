package com.example.appointments_app.model;

import java.util.List;
import java.util.Set;

public final class BusinessBuilder {
    private Long id;
    private String businessName;
    private User owner;
    private Integer totalAppointments;
    private Set<Appointment> availableAppointments;
    private List<Service> services;

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

    public BusinessBuilder withAvailableAppointments(Set<Appointment> availableAppointments) {
        this.availableAppointments = availableAppointments;
        return this;
    }

    public BusinessBuilder withServices(List<Service> services) {
        this.services = services;
        return this;
    }

    public Business build() {
        Business business = new Business();
        business.setId(id);
        business.setBusinessName(businessName);
        business.setOwner(owner);
        business.setTotalAppointments(totalAppointments);
        business.setAvailableAppointments(availableAppointments);
        business.setServices(services);
        return business;
    }
}
