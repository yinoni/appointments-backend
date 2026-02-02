package com.example.appointments_app.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="business_id")
    private Business business;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer duration;

    public Service() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
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

    public ServiceDTO convertToDTO(){
        ServiceDTO dto = new ServiceDTO();

        dto.setId(this.id);
        dto.setDuration(this.duration);
        dto.setPrice(this.price);
        dto.setServiceName(this.serviceName);

        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id) && Objects.equals(business, service.business) && Objects.equals(serviceName, service.serviceName) && Objects.equals(price, service.price) && Objects.equals(duration, service.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, business, serviceName, price, duration);
    }
}
