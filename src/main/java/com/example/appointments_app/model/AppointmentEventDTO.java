package com.example.appointments_app.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentEventDTO implements Serializable {
    private String fullname;
    private String phone;
    private LocalTime time;
    private String businessName;
    private LocalDate date;
    private String serviceName;
    private Long serviceId;
    private Double price;


    public AppointmentEventDTO(String fullname,
                               String phone,
                               LocalTime time,
                               String businessName,
                               LocalDate date,
                               String serviceName,
                               Long serviceId,
                               Double price) {
        this.fullname = fullname;
        this.phone = phone;
        this.time = time;
        this.businessName = businessName;
        this.date = date;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.price = price;
    }

    public AppointmentEventDTO() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public AppointmentIndex toIndex(){
        AppointmentIndex index = new AppointmentIndex();
        index.setBusinessName(this.businessName);
        index.setStatus("COMPLETED");
        index.setServiceName(this.serviceName);
        index.setTimeCreated(LocalDateTime.of(this.date, this.time));
        index.setServiceId(this.serviceId);
        index.setServicePrice(this.price);

        return index;
    }
}
