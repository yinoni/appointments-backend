package com.example.appointments_app.service;

import com.example.appointments_app.exception.ServiceNotFoundException;
import com.example.appointments_app.model.Appointment;
import com.example.appointments_app.model.Schedule;
import com.example.appointments_app.model.ServiceDTO;
import com.example.appointments_app.model.ServiceIn;
import com.example.appointments_app.repo.ServiceRepo;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepo serviceRepo;

    public ServiceService(ServiceRepo serviceRepo){
        this.serviceRepo = serviceRepo;
    }


    public com.example.appointments_app.model.Service findById(Long serviceId) {
        return serviceRepo.findById(serviceId).orElseThrow(() ->
                new ServiceNotFoundException("Service not exists!"));
    }

    public com.example.appointments_app.model.Service addNewService(com.example.appointments_app.model.Service service){
        return serviceRepo.save(service);
    }

}
