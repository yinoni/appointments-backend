package com.example.appointments_app.repo;

import com.example.appointments_app.model.Appointment;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppointmentRepo extends CrudRepository<Appointment, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.service = null WHERE a.service.id = :serviceId")
    void nullifyServiceInAppointments(@Param("serviceId") Long serviceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.service.id IN " +
            "(SELECT s.id FROM Service s WHERE s.business.id = :businessId)")
    void deleteAllAppointmentsByBusinessId(@Param("businessId") Long businessId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.service.id = :serviceId")
    void deleteAppointmentByServiceId(@Param("serviceId") Long serviceId);
}
