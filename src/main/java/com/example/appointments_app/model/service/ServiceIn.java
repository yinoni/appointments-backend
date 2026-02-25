package com.example.appointments_app.model.service;

import static com.example.appointments_app.model.service.ServiceBuilder.aService;

public class ServiceIn {
    private Long businessId;
    private String serviceName;
    private Double price;
    private Integer duration;

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Service toService(){
        return aService()
                .withDuration(this.duration)
                .withPrice(this.price)
                .withServiceName(this.serviceName)
                .build();
    }
}
