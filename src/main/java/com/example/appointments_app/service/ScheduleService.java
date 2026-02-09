package com.example.appointments_app.service;

import com.example.appointments_app.exception.BusinessException;
import com.example.appointments_app.exception.ScheduleNotFoundException;
import com.example.appointments_app.model.CustomUserDetails;
import com.example.appointments_app.model.Schedule;
import com.example.appointments_app.model.ScheduleDTO;
import com.example.appointments_app.model.ScheduleIn;
import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.repo.ScheduleRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // בשביל הפרמטר
import org.springframework.data.domain.PageRequest; // בשביל יצירת הבקשה
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;
    private final Redis redis;

    public ScheduleService(ScheduleRepo scheduleRepo, Redis redis)
    {
        this.scheduleRepo = scheduleRepo;
        this.redis = redis;

    }

    public Schedule findById(Long scheduleId){
        return scheduleRepo.findById(scheduleId).orElseThrow(() ->
                new ScheduleNotFoundException("Schedule with id " + scheduleId + " not found"));
    }

    public void deleteById(Long scheduleId){
        scheduleRepo.deleteById(scheduleId);
    }

    public Schedule addNewSchedule(Schedule schedule){
        Schedule result = scheduleRepo.save(schedule);
        String key = result.getBusiness().getId() + ":" + result.getId() + ":" + result.getDate();
        redis.setOffsetsPipelined(key, result.getAvailable_hours());
        return result;
    }

    public Schedule getScheduleByDate(Long businessId, LocalDate date){
        return scheduleRepo.findScheduleByDateAndBusiness(businessId, date).orElseThrow(() ->
                new ScheduleNotFoundException(("The schedule not found!")));
    }

    public boolean tryToLockSlot(Schedule schedule, LocalTime time, int serviceDuration){
        String key = schedule.getBusiness().getId() + ":" + schedule.getId() + ":" + schedule.getDate();
        return redis.tryToLockSlot(key, time, serviceDuration);
    }

    public Page<Schedule> getSchedulesByBusinessId(Long b_id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());
        return scheduleRepo.getSchedulesByBusinessId(b_id, pageable);
    }

    public List<LocalTime> getAvailableHours(Long scheduleId){
        Schedule schedule = findById(scheduleId);
        long startOffset = redis.getOffset(schedule.getStart_time());
        long endOffset = redis.getOffset(schedule.getEnd_time());

        List<LocalTime> availableHours = redis.getHoursFromOffsetRange(schedule.getKey(), startOffset, endOffset, schedule.getMin_duration());

        return availableHours;
    }
}
