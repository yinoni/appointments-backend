package com.example.appointments_app.repo;

import com.example.appointments_app.model.Schedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepo extends CrudRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.business.id = :b_id")
    List<Schedule> getSchedulesByBusinessId(@Param("b_id") Long b_id);
}
