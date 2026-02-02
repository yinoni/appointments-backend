package com.example.appointments_app.model;

public final class ServiceBuilder {
    private Long id;
    private Business business;
    private String serviceName;
    private Double price;
    private Integer duration;

    private ServiceBuilder() {
    }

    public static ServiceBuilder aService() {
        return new ServiceBuilder();
    }

    public ServiceBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ServiceBuilder withBusiness(Business business) {
        this.business = business;
        return this;
    }

    public ServiceBuilder withServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public ServiceBuilder withPrice(Double price) {
        this.price = price;
        return this;
    }

    public ServiceBuilder withDuration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public Service build() {
        Service service = new Service();
        service.setId(id);
        service.setBusiness(business);
        service.setServiceName(serviceName);
        service.setPrice(price);
        service.setDuration(duration);
        return service;
    }
}
