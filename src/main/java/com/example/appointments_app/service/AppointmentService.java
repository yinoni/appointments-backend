package com.example.appointments_app.service;

import com.example.appointments_app.exception.AppointmentAlreadyExistsException;
import com.example.appointments_app.exception.BusinessException;
import com.example.appointments_app.exception.ServiceNotFoundException;
import com.example.appointments_app.kafka.AppointmentProducer;
import com.example.appointments_app.model.*;
import com.example.appointments_app.repo.AppointmentRepo;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final UserService userService;
    private final ServiceService serviceService;
    private final ScheduleService scheduleService;
    private final AppointmentRepo appointmentRepo;
    private final AppointmentProducer appointmentProducer;

    public AppointmentService(UserService userService,
                              AppointmentRepo appointmentRepo,
                              ServiceService serviceService,
                              ScheduleService scheduleService,
                              AppointmentProducer appointmentProducer){
        this.appointmentRepo = appointmentRepo;
        this.serviceService = serviceService;
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.appointmentProducer = appointmentProducer;
    }

    public Appointment insertAppointment(AppointmentIn appointmentIn) {
        Appointment app = appointmentIn.toAppointment();
        Schedule schedule = scheduleService.findById(appointmentIn.getScheduleId());
        com.example.appointments_app.model.Service service = serviceService.findById(appointmentIn.getServiceId());
        User user = userService.findByPhone(appointmentIn.getPhone());
        AppointmentEventDTO event;

        if(schedule.getBusiness().getId() != service.getBusiness().getId())
            throw new BusinessException("The business doesn't have the schedule or the service!", HttpStatus.BAD_REQUEST);

        app.setSchedule(schedule);
        app.setService(service);
        app.setUser(user);

        if(!scheduleService.tryToLockSlot(schedule, appointmentIn.getTime(), service.getDuration()))
            throw new AppointmentAlreadyExistsException("The appointment is taken!");

        app = appointmentRepo.save(app);

        event = new AppointmentEventDTO(
                user.getFullName(),
                user.getPhoneNumber(),
                app.getTime(),
                schedule.getBusiness().getBusinessName(),
                schedule.getDate(),
                service.getServiceName()
        );

        appointmentProducer.sendAppointmentEvent(event);

        return app;
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
