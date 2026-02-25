package com.example.appointments_app.model.appointment;

import com.example.appointments_app.model.schedule.Schedule;
import com.example.appointments_app.model.service.Service;
import com.example.appointments_app.model.user.User;
import jakarta.persistence.*;

import java.time.LocalTime;


@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime time;

    @ManyToOne
    @JoinColumn(name="service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String status;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public Appointment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public AppointmentDTO convertToDTO(){
        AppointmentDTO dto = new AppointmentDTO();

        dto.setId(this.id);
        dto.setTime(this.time);
        dto.setService(this.service.convertToDTO());
        dto.setUser(this.user.convertToUserDTO());

        return dto;
    }
}
