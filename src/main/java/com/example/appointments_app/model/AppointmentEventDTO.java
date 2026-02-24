package com.example.appointments_app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentEventDTO implements Serializable {
    private Long businessId;
    private Long appointmentId;
    private String businessName;
    private Long serviceId;
    private String phoneNumber;

    // נתונים לצורך האגרגציה ב-Elastic
    private Double price;
    private LocalDateTime appointmentDate;

    // Metadata - קריטי לניהול האירוע
    private String eventType; // למשל: "CREATED", "CANCELLED", "UPDATED"

    private boolean newCustomer;


    public AppointmentEventDTO(Long businessId,
                               Long appointmentId,
                               String businessName,
                               Long serviceId,
                               Double price,
                               LocalDateTime appointmentDate,
                               String eventType,
                               String phoneNumber,
                               boolean newCustomer) {
        this.businessId = businessId;
        this.appointmentId = appointmentId;
        this.businessName = businessName;
        this.serviceId = serviceId;
        this.price = price;
        this.appointmentDate = appointmentDate;
        this.eventType = eventType;
        this.phoneNumber = phoneNumber;
        this.newCustomer = newCustomer;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(boolean newCustomer) {
        this.newCustomer = newCustomer;
    }

    public AppointmentIndex toIndex(){
        AppointmentIndex index = new AppointmentIndex();
        index.setBusinessId(this.businessId);
        index.setServiceId(this.serviceId);
        index.setBusinessName(this.businessName);
        index.setServicePrice(this.price);
        index.setTimeCreated(this.appointmentDate);
        index.setStatus(this.eventType);
        index.setFirstTimeCustomer(this.newCustomer);

        return index;
    }
}
