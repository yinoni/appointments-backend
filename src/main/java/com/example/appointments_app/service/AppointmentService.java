package com.example.appointments_app.service;

import com.example.appointments_app.exception.ServiceNotFoundException;
import com.example.appointments_app.model.Appointment;
import com.example.appointments_app.repo.AppointmentRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;

    public AppointmentService(AppointmentRepo appointmentRepo){
        this.appointmentRepo = appointmentRepo;
    }

    public Appointment insertAppointment(Appointment app) {
        return appointmentRepo.save(app);
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
