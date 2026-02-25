package com.example.appointments_app.model.business;

import com.example.appointments_app.model.service.ServiceDTO;
import com.example.appointments_app.model.user.UserDTO;

import java.util.List;

public class BusinessDTO {
    private Long id;
    private String businessName;
    private String description;
    private String address;
    private UserDTO owner;
    private Integer totalAppointments;
    private List<ServiceDTO> services;
    private Double rating;
    private String tagline;
    private String category;


    public BusinessDTO(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public UserDTO getOwner() {
        return owner;
    }

    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }


    public List<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
