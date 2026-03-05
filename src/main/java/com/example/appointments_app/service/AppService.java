package com.example.appointments_app.service;

import com.example.appointments_app.exception.BusinessException;
import com.example.appointments_app.exception.ScheduleNotFoundException;
import com.example.appointments_app.exception.UserNotFoundException;
import com.example.appointments_app.model.ScreensDTO.HomeDTO;
import com.example.appointments_app.model.ScreensDTO.InsightsDTO;
import com.example.appointments_app.model.appointment.Appointment;
import com.example.appointments_app.model.appointment.AppointmentDTO;
import com.example.appointments_app.model.business.Business;
import com.example.appointments_app.model.business.BusinessDTO;
import com.example.appointments_app.model.business.BusinessSummary;
import com.example.appointments_app.model.data_aggregation.RevenueData;
import com.example.appointments_app.model.schedule.ScheduleDTO;
import com.example.appointments_app.model.user.User;
import com.example.appointments_app.repo.AppointmentRepo;
import com.example.appointments_app.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AppService {

    private static final Logger log = LoggerFactory.getLogger(AppService.class);
    private final AuthService authService;
    private final BusinessService businessService;
    @Lazy
    private final AppointmentRepo appointmentRepo;
    @Lazy
    private final AnalyticsService analyticsService;

    private final UserRepository userRepository;


    public AppService(AuthService authService,
                         BusinessService businessService,
                      AppointmentRepo appointmentRepo,
                      AnalyticsService analyticsService,
                      UserRepository userRepository) {
        this.authService = authService;
        this.businessService = businessService;
        this.appointmentRepo = appointmentRepo;
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
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

    /***
     *
     * @param userId - The current user ID
     * @param businessId - The business ID
     * @return - True if the business is not exists in the favorites array and false if else
     */
    @Transactional
    public boolean toggleFavorite(Long userId, Long businessId){
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user not found!", HttpStatus.BAD_REQUEST));
        Business business = businessService.findBusinessById(businessId);
        Set<Business> favorites = user.getSavedBusinesses();
        boolean inserted = false;

        if(favorites.contains(business))
            favorites.remove(business);

        else{
            favorites.add(business);
            inserted = true;
        }

        userRepository.save(user);
        return inserted;
    }

    
}
