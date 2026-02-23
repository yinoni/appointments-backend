package com.example.appointments_app.service;

import com.example.appointments_app.elasticsearch.ElasticSearchService;
import com.example.appointments_app.exception.ScheduleNotFoundException;
import com.example.appointments_app.model.*;
import com.example.appointments_app.model.data_aggregation.WeeklyRevenueData;
import com.example.appointments_app.repo.AppointmentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppService {

    private static final Logger log = LoggerFactory.getLogger(AppService.class);
    private final AuthService authService;
    private final BusinessService businessService;
    @Lazy
    private final AppointmentRepo appointmentRepo;
    private final ElasticSearchService esService;

    public AppService(AuthService authService,
                         BusinessService businessService,
                      AppointmentRepo appointmentRepo,
                      ElasticSearchService esService) {
        this.authService = authService;
        this.businessService = businessService;
        this.appointmentRepo = appointmentRepo;
        this.esService = esService;
    }

    public HomeDTO getHomePageDTO(Long userId){
        List<BusinessDTO> businesses = businessService.getBusinessesByOwnerId(userId);
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        List<WeeklyRevenueData> revenueDataList = new ArrayList<>();

        if (businesses.isEmpty()) {
            log.info("No businesses found for user {}", userId);
            return new HomeDTO(businesses, appointmentDTOS, revenueDataList);
        }
        Long businessId = businesses.get(0).getId();
        try{
            revenueDataList = esService.getWeeklyRevenue(businessId);
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }


        try{
            ScheduleDTO scheduleDTO = businessService.findScheduleByDateAndBusiness(businessId, LocalDate.now());
            Page<Appointment> todayAppointments = appointmentRepo.getAppointmentsByScheduleId(scheduleDTO.getId(), PageRequest.of(0, 5));
            appointmentDTOS = todayAppointments.stream().map(Appointment::convertToDTO).toList();
        }
        catch(ScheduleNotFoundException ex){
            log.info("There is no schedule for business {} on today's date", businessId);
        }

        return new HomeDTO(businesses, appointmentDTOS, revenueDataList);
    }
}
