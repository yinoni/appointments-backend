package com.example.appointments_app.service;

import com.example.appointments_app.elasticsearch.ElasticSearchService;
import com.example.appointments_app.exception.*;
import com.example.appointments_app.kafka.BusinessProducer;
import com.example.appointments_app.model.appointment.Appointment;
import com.example.appointments_app.model.appointment.AppointmentDTO;
import com.example.appointments_app.model.business.*;
import com.example.appointments_app.model.schedule.Schedule;
import com.example.appointments_app.model.schedule.ScheduleDTO;
import com.example.appointments_app.model.schedule.ScheduleIn;
import com.example.appointments_app.model.service.ServiceDTO;
import com.example.appointments_app.model.service.ServiceIn;
import com.example.appointments_app.model.user.User;
import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.repo.AppointmentRepo;
import com.example.appointments_app.repo.BusinessRepo;
import com.example.appointments_app.repo.ServiceRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.example.appointments_app.model.business.BusinessBuilder.aBusiness;

@Service
public class BusinessService {

    private static final Logger log = LoggerFactory.getLogger(BusinessService.class);
    private final BusinessRepo businessRepo;
    private final AppointmentRepo appointmentRepo;
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final BusinessProducer businessProducer;
    private final ElasticSearchService elasticSearchService;
    @Lazy
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    @Lazy
    private RedisTemplate redisTemplate;
    private static final String BUSINESS_NAMES_SET = "all_business_names";
    private static final int PAGE_SIZE = 10;


    public BusinessService(BusinessRepo businessRepo,
                           AppointmentRepo appointmentRepo,
                           UserService userService,
                           ScheduleService scheduleService,
                           ElasticSearchService elasticSearchService,
                           BusinessProducer businessProducer,
                           ObjectMapper objectMapper,
                           ModelMapper modelMapper,
                           RedisTemplate redisTemplate){
        this.businessRepo = businessRepo;
        this.appointmentRepo = appointmentRepo;
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.businessProducer = businessProducer;
        this.elasticSearchService = elasticSearchService;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.redisTemplate = redisTemplate;
    }

    /***
     *
     * @param b_id - The business id
     * @return - The business with id of b_id
     */
    public Business findBusinessById(Long b_id){
        return businessRepo.findById(b_id).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));
    }

    public Business save(Business business){
        return businessRepo.save(business);
    }

    public Business updateBusiness(Long b_id, Long ownerId, BusinessInput businessInput){
        Business prevBusiness = findBusinessByIdAndOwnerId(b_id, ownerId);
        String prevName = prevBusiness.getBusinessName().trim().toLowerCase();
        String newName = businessInput.getBusinessName().trim().toLowerCase();

        if(!prevName.equals(newName)){
            if(isBusinessNameExists(newName))
                throw new BusinessException("Business name already exists", HttpStatus.CONFLICT);
        }

        redisTemplate.opsForSet().remove(BUSINESS_NAMES_SET, prevName);
        redisTemplate.opsForSet().add(BUSINESS_NAMES_SET, newName);

        modelMapper.map(businessInput, prevBusiness);

        Business newBusiness = save(prevBusiness);

        businessProducer.sendBusinessUpdatedEvent(newBusiness.convertToDTO());

        return newBusiness;
    }

    public boolean isBusinessNameExists(String businessName){
        String normalizedName = businessName.trim().toLowerCase();
        Boolean isMember = redisTemplate.opsForSet().isMember(BUSINESS_NAMES_SET, normalizedName);

        return Boolean.TRUE.equals(isMember);
    }

    /***
     *
     * @param businessInput - see BusinessIn class
     * @param ownerId - The owner id
     * @return - Create a new business with list of services and returns DTO of the business that added
     */
    @Transactional
    public BusinessDTO createBusiness(BusinessInput businessInput, Long ownerId){
        String normalizedName = businessInput.getBusinessName().trim().toLowerCase();

        if(isBusinessNameExists(businessInput.getBusinessName()))
            throw new BusinessException("Business name already exists", HttpStatus.CONFLICT);

        User user = userService.findById(ownerId);
        BusinessDTO bDTO;

        Business business = businessInput.toBusiness();
        final Business finalBusiness = business;
        business.setOwner(user);

        if(businessInput.getServices() != null){
            List<com.example.appointments_app.model.service.Service> services =
                    businessInput.getServices().stream().map(service -> {
                        var s = service.toService();
                        s.setBusiness(finalBusiness);
                        return s;
                    }).toList();

            business.setServices(services);
        }

        business = businessRepo.save(business);
        bDTO = business.convertToDTO();

        redisTemplate.opsForSet().add(BUSINESS_NAMES_SET, normalizedName);

        businessProducer.sendBusinessCreatedEvent(bDTO);

        return  bDTO;
    }

    public boolean findByBusinessName(String businessName){
        return businessRepo.findByBusinessName(businessName).isPresent();
    }

    /***
     *
     * @param businessId - The business id
     * @param ownerId - The id of the business owner
     * @return - The business that found by business id and owner id and throw BusinessException if the business is not found or if the owner is not the business owner
     */
    public Business findBusinessByIdAndOwnerId(Long businessId, Long ownerId){
        Business business = businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        if(!Objects.equals(ownerId, business.getOwner().getId()))
            throw new BusinessException("The owner is not allowed!", HttpStatus.FORBIDDEN);

        return business;
    }

    public List<BusinessDTO> getBusinessesByOwnerId(Long ownerId){
        List<Business> businesses = businessRepo.findAllByOwnerId(ownerId);

        return businesses.stream().map(Business::convertToDTO).toList();
    }

    public List<BusinessDTO> searchBusiness(BusinessSearchRequest businessSearchRequest){
        ObjectMapper mapper = new ObjectMapper();
        String query = businessSearchRequest.getQuery();
        int from = businessSearchRequest.getFrom();
        List<BusinessDTO> businesses = new ArrayList<>();

        try{
            List<Map<String, Object>> mustList = new ArrayList<>();
            if (query != null && !query.isEmpty()) {
                mustList.add(Map.of("multi_match", Map.of(
                        "query", query,
                        "fields", List.of("businessName", "category")
                )));
            } else {
                mustList.add(Map.of("match_all", Map.of())); // אם אין שאילתה, שלוף הכל
            }

            Map<String, Object> boolQuery = new HashMap<>();
            boolQuery.put("must", mustList);

            if(businessSearchRequest.getFilters() != null)
                boolQuery.put("filter", businessSearchRequest.getFilters().generateFilterList());

            Map<String, Object> root = new HashMap<>();
            root.put("from", from * PAGE_SIZE);
            root.put("size", PAGE_SIZE);
            root.put("query", Map.of("bool", boolQuery));
            root.put("sort", List.of(Map.of("rating", "asc")));

            String jsonBody = mapper.writeValueAsString(root);

            String res = elasticSearchService.aggregate("businesses", jsonBody);

            JsonNode hits = fetchSearchResult(res);

            for(JsonNode node : hits){
                businesses.add(objectMapper.treeToValue(node.path("_source"), BusinessDTO.class));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return businesses;

    }


    /***
     *
     * @param businessId - The business id
     * @param userId - The user id
     * @return - DTO of the deleted business
     */
    public BusinessDTO deleteBusiness(Long businessId, Long userId){
        Business business = businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        BusinessDTO bDTO;

        if(!Objects.equals(business.getOwner().getId(), userId))
            throw new BusinessException("User not allowed to delete this business!", HttpStatus.FORBIDDEN);

        appointmentRepo.deleteAllAppointmentsByBusinessId(businessId);

        bDTO = business.convertToDTO();

        businessRepo.deleteById(businessId);

        redisTemplate.opsForSet().remove(BUSINESS_NAMES_SET, business.getBusinessName());

        businessProducer.sendBusinessDeletedEvent(bDTO);

        return bDTO;
    }



    /***
     *
     * @param businessId - The business id
     * @param scheduleIn - Schedule input (See ScheduleIn class)
     * @param ownerId - The id of the owner
     * @return - new schedule dto of the new schedule
     */
    public ScheduleDTO addNewSchedule(Long businessId, ScheduleIn scheduleIn, Long ownerId){

        Business business = findBusinessByIdAndOwnerId(businessId, ownerId);
        Schedule schedule = scheduleIn.toSchedule();
        ScheduleDTO dto;

        schedule.setBusiness(business);
        schedule.setAppointments(new ArrayList<>());

        schedule = scheduleService.addNewSchedule(schedule);

        dto = schedule.convertToDTO();
        dto.setHours(scheduleService.getAvailableHours(schedule.getId()));

        return dto;
    }

    /***
     *
     * @param businessId - The business id
     * @return - All list of all the business schedules
     */
    public List<ScheduleDTO> findAllBusinessSchedules(Long businessId, int pageNumber, int size){
        businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        Page<Schedule> page = scheduleService.getSchedulesByBusinessId(businessId, pageNumber, size);
        List<Schedule> schedules = page.stream().toList();

        return  schedules.stream().map(Schedule::convertToDTO).toList();
    }

    /**
     *
     * @param businessId - The id of the business
     * @param scheduleId - The schedule id
     * @param ownerId - The id of the current user
     *                This function delete schedule by the schedule id
     */
    public void deleteSchedule(Long businessId, Long scheduleId, Long ownerId){
        findBusinessByIdAndOwnerId(businessId, ownerId);
        Schedule schedule = scheduleService.findById(scheduleId);

        if(!Objects.equals(schedule.getBusiness().getId(), businessId))
            throw new BusinessException("The business not contains this schedule!", HttpStatus.BAD_GATEWAY);

        scheduleService.deleteById(scheduleId);
    }

    /***
     *
     * @param businessId - The business id
     * @param date - The date of the schedule
     * @return - The schedule of business {businessId} and with the date {date}
     */
    public ScheduleDTO findScheduleByDateAndBusiness(Long businessId, LocalDate date, int pageNum, int records) {
        findBusinessById(businessId);
        Schedule schedule = scheduleService.getScheduleByDate(businessId, date);
        ScheduleDTO dto = schedule.convertToDTO();
        Map<LocalTime, Boolean> availableHours = scheduleService.getAvailableHours(schedule.getId());
        int finalRecords = records == -1 ? Integer.MAX_VALUE : records;

        Page<Appointment> appointmentsPagination = appointmentRepo.getAppointmentsByScheduleId(schedule.getId(), PageRequest.of(pageNum, finalRecords));
        List<AppointmentDTO> appointmentDTOS = appointmentsPagination.stream().map(Appointment::convertToDTO).toList();

        dto.setHours(availableHours);
        dto.setAppointments(appointmentDTOS);

        return dto;
    }

    /***
     *
     * @param category - The category name that we will filter by
     * @param rating - The rating that we will filter by
     * @param distance - The distance that we will filter by (Not available)
     * @return - List of all the businesses with category {category} and reating bigger than {Rating}
     */
    public List<BusinessDTO> filterBusinesses(String category, Double rating, Double distance){
        List<BusinessDTO> businessDTOS = new ArrayList<>();
        // 1. רשימה שתחזיק את חלקי הפילטרים
        List<String> filters = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            filters.add(String.format("{\"match\": {\"category\": \"%s\"}}", category));
        }

        if (rating != null && rating > 0) {
            filters.add(String.format("{\"range\": {\"rating\": {\"gte\": %.1f}}}", rating));
        }

        // 2. חיבור הפילטרים למחרוזת אחת עם פסיק ביניהם
        String filterSection = String.join(",", filters);

        // 3. בניית ה-JSON הסופי - פעם אחת בלבד!
        String finalPipeline = String.format("""
        {
          "size": 20,
          "query": {
            "bool": {
              "filter": [ %s ]
            }
          }
        }
        """, filterSection);

        String response = elasticSearchService.aggregate("businesses", finalPipeline);

        JsonNode root = objectMapper.readTree(response);
        JsonNode hits = root.path("hits").path("hits");

        for(JsonNode node : hits){
            JsonNode source = node.path("_source");
            businessDTOS.add(objectMapper.treeToValue(source, BusinessDTO.class));
        }

        return businessDTOS;
    }

    public JsonNode fetchSearchResult(String res){
        List<Object> object = new ArrayList<>();
        JsonNode root = objectMapper.readTree(res);
        JsonNode hits = root.path("hits").path("hits");

        return hits;
    }
}
