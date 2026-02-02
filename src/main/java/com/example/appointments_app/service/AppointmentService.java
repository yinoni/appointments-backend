package com.example.appointments_app.service;

import com.example.appointments_app.exception.ServiceNotFoundException;
import com.example.appointments_app.repo.AppointmentRepo;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;

    public AppointmentService(AppointmentRepo appointmentRepo){
        this.appointmentRepo = appointmentRepo;
    }

    public void deleteAllAppointmentsByBusinessId(Long businessId){
        appointmentRepo.deleteAllAppointmentsByBusinessId(businessId);
    }

    public void setServiceNullInAppointment(Long serviceId){
        appointmentRepo.nullifyServiceInAppointments(serviceId);
    }

    public void deleteAppointmentByServiceId(Long serviceId, Long ownerId) {

        appointmentRepo.deleteAppointmentByServiceId(serviceId);
    }
}
