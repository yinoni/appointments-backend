package com.example.appointments_app.model.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class AppointmentIndex {
    private Long businessId;
    private String businessName;
    private Long serviceId;
    private String serviceName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeCreated;
    private String status;
    private Double servicePrice;
    private boolean firstTimeCustomer;

    public AppointmentIndex() {
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }


    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Double getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(Double servicePrice) {
        this.servicePrice = servicePrice;
    }

    public boolean getFirstTimeCustomer() {
        return firstTimeCustomer;
    }

    public void setFirstTimeCustomer(boolean setFirstTimeCustomer) {
        this.firstTimeCustomer = setFirstTimeCustomer;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isFirstTimeCustomer() {
        return firstTimeCustomer;
    }
}
