package com.example.appointments_app.model;

import java.util.List;
import java.util.Set;

public class BusinessDTO {
    private Long id;
    private String businessName;
    private UserDTO owner;
    private Integer totalAppointments;
    private Set<AppointmentDTO> availableAppointments;
    private List<ServiceDTO> services;


    public BusinessDTO(){}

    public BusinessDTO(Long id, String businessName, UserDTO owner, Integer totalAppointments, Set<AppointmentDTO> availableAppointments, List<ServiceDTO> services) {
        this.id = id;
        this.businessName = businessName;
        this.owner = owner;
        this.totalAppointments = totalAppointments;
        this.availableAppointments = availableAppointments;
        this.services = services;
    }

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

    public Set<AppointmentDTO> getAvailableAppointments() {
        return availableAppointments;
    }

    public void setAvailableAppointments(Set<AppointmentDTO> availableAppointments) {
        this.availableAppointments = availableAppointments;
    }

    public List<ServiceDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceDTO> services) {
        this.services = services;
    }
}
