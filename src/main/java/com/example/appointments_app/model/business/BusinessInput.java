package com.example.appointments_app.model.business;

import com.example.appointments_app.model.service.ServiceIn;

import java.util.ArrayList;
import java.util.List;

import static com.example.appointments_app.model.business.BusinessBuilder.aBusiness;

public class BusinessInput {

    private String businessName;
    private String businessDesc;
    private String city;
    private String street;
    private String category;
    private String tagline;
    private List<ServiceIn> services;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public List<ServiceIn> getServices() {
        return services;
    }

    public void setServices(List<ServiceIn> services) {
        this.services = services;
    }

    public Business toBusiness() {
        return aBusiness()
                .withBusinessName(this.businessName)
                .withDescription(this.businessDesc)
                .withCity(this.city)
                .withStreet(this.street)
                .withCategory(BusinessCategory.fromString(this.category.toUpperCase()))
                .withTotalAppointments(0)
                .withSchedules(new ArrayList<>())
                .withServices(new ArrayList<>())
                .withRating(2.5)
                .withTagline(this.tagline)
                .build();
    }
}
