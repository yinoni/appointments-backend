package com.example.appointments_app.model;

import java.time.LocalDateTime;

public class AppointmentDTO {
    private Long id;
    private LocalDateTime time;
    private ServiceDTO service;
    private UserDTO user;

    public AppointmentDTO(){}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public ServiceDTO getService() {
        return service;
    }

    public void setService(ServiceDTO service) {
        this.service = service;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
