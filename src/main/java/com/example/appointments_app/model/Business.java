package com.example.appointments_app.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String businessName;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    private Integer totalAppointments;

    @OneToMany
    private Set<Appointment> availableAppointments;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services;

    public Business() {
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Set<Appointment> getAvailableAppointments() {
        return availableAppointments;
    }

    public void setAvailableAppointments(Set<Appointment> availableAppointments) {
        this.availableAppointments = availableAppointments;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public BusinessDTO convertToDTO(){
        BusinessDTO dto = new BusinessDTO();
        List<ServiceDTO> services = new ArrayList<>();
        Set<AppointmentDTO> availableAppointments = new HashSet<>();
        dto.setId(this.id);
        dto.setBusinessName(this.businessName);
        dto.setOwner(this.owner.convertToUserDTO());
        dto.setTotalAppointments(this.totalAppointments);

        services = this.services.stream().map(Service::convertToDTO).toList();
        availableAppointments = this.availableAppointments.stream().map(Appointment::convertToDTO).collect(Collectors.toSet());

        dto.setAvailableAppointments(availableAppointments);
        dto.setServices(services);

        return dto;
    }
}
