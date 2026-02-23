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

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    @Column(nullable = false, name="description", columnDefinition = "varchar(255) default 'N/A'")
    private String description;

    @Column(nullable = false, name = "city", columnDefinition = "varchar(255) default 'N/A'")
    private String city;

    @Column(nullable = false, name = "street", columnDefinition = "varchar(255) default 'N/A'")
    private String street;


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


    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void increaseTotalAppointments() {
        this.totalAppointments += 1;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BusinessDTO convertToDTO(){
        BusinessDTO dto = new BusinessDTO();
        List<ServiceDTO> services = this.services.stream().map(Service::convertToDTO).toList();
        List<ScheduleDTO> schedules = this.schedules.stream().map(Schedule::convertToDTO).toList();

        dto.setId(this.id);
        dto.setBusinessName(this.businessName);
        dto.setDescription(this.description);
        dto.setAddress(this.city + ", " + this.street);
        dto.setOwner(this.owner.convertToUserDTO());
        dto.setTotalAppointments(this.totalAppointments);
        dto.setServices(services);

        return dto;
    }
}
