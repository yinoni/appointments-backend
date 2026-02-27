package com.example.appointments_app.service;

import com.example.appointments_app.exception.ScheduleNotFoundException;
import com.example.appointments_app.model.ScreensDTO.HomeDTO;
import com.example.appointments_app.model.ScreensDTO.InsightsDTO;
import com.example.appointments_app.model.appointment.Appointment;
import com.example.appointments_app.model.appointment.AppointmentDTO;
import com.example.appointments_app.model.business.Business;
import com.example.appointments_app.model.business.BusinessDTO;
import com.example.appointments_app.model.business.BusinessSummary;
import com.example.appointments_app.model.data_aggregation.RevenueData;
import com.example.appointments_app.model.schedule.ScheduleDTO;
import com.example.appointments_app.repo.AppointmentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
    @Lazy
    private final AnalyticsService analyticsService;

    public AppService(AuthService authService,
                         BusinessService businessService,
                      AppointmentRepo appointmentRepo,
                      AnalyticsService analyticsService) {
        this.authService = authService;
        this.businessService = businessService;
        this.appointmentRepo = appointmentRepo;
        this.analyticsService = analyticsService;
    }

    /***
     *
     * @param userId - The current user ID
     * @return - HomeDTO that contains all the information about the first business
     */
    public HomeDTO getOwnerHomePageDTO(Long userId){
        List<BusinessDTO> businesses = businessService.getBusinessesByOwnerId(userId);
        BusinessSummary businessSummary = new BusinessSummary();
        if (businesses.isEmpty()) {
            log.info("No businesses found for user {}", userId);
            return new HomeDTO(businesses, businessSummary);
        }
        Long businessId = businesses.get(0).getId();

        businessSummary = getBusinessSummary(userId, businessId);

        return new HomeDTO(businesses, businessSummary);
    }

    /***
     *
     * @param userId - The user ID
     * @param businessId - The business ID
     * @return - Summary of the business (Today's appointments and weekly revenue data)
     */
    public BusinessSummary getBusinessSummary(Long userId, Long businessId){
        businessService.findBusinessByIdAndOwnerId(businessId, userId);
        List<AppointmentDTO> appointmentDTOS = new ArrayList<>();
        List<RevenueData> revenueDataList = new ArrayList<>();

        revenueDataList = analyticsService.getRevenueAnalytics(businessId, "7_DAYS");

        try{
            ScheduleDTO scheduleDTO = businessService.findScheduleByDateAndBusiness(businessId, LocalDate.now());
            Page<Appointment> todayAppointments = appointmentRepo.getAppointmentsByScheduleId(scheduleDTO.getId(), PageRequest.of(0, 5));
            appointmentDTOS = todayAppointments.stream().map(Appointment::convertToDTO).toList();
        }
        catch(ScheduleNotFoundException ex){
            log.info("There is no schedule for business {} on today's date", businessId);
        }

        return new BusinessSummary(appointmentDTOS, revenueDataList);
    }

    public InsightsDTO getInsightsPageDTO(Long ownerId, Long businessId, String userSelection){
        Business business = businessService.findBusinessByIdAndOwnerId(businessId, ownerId);
        return analyticsService.getInsightsPageData(businessId, userSelection);
    }
}
