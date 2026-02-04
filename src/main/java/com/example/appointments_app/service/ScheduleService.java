package com.example.appointments_app.service;

import com.example.appointments_app.exception.BusinessException;
import com.example.appointments_app.model.Schedule;
import com.example.appointments_app.model.ScheduleDTO;
import com.example.appointments_app.model.ScheduleIn;
import com.example.appointments_app.repo.ScheduleRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepo scheduleRepo;

    public ScheduleService(ScheduleRepo scheduleRepo) {
        this.scheduleRepo = scheduleRepo;
    }

    public Schedule addNewSchedule(Schedule schedule){
        return scheduleRepo.save(schedule);
    }

    public List<Schedule> getSchedulesByBusinessId(Long b_id) {
        return scheduleRepo.getSchedulesByBusinessId(b_id);
    }
}
