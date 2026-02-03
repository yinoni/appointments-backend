package com.example.appointments_app.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    @OneToMany(mappedBy = "schedule",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    public Schedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public ScheduleDTO convertToDTO(){
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(id);
        scheduleDTO.setDate(date);
        scheduleDTO.setBusiness(business.convertToDTO());
        scheduleDTO.setAppointments(appointments.stream().map(Appointment::convertToDTO).toList());

        return scheduleDTO;
    }


}
