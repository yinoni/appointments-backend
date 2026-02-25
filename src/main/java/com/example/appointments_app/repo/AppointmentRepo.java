package com.example.appointments_app.repo;

import com.example.appointments_app.model.appointment.Appointment;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AppointmentRepo extends CrudRepository<Appointment, Long> {

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.user.phoneNumber = :phone")
    boolean existsByCustomerPhone(@Param("phone") String phone);

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


    @Query("SELECT a FROM Appointment a WHERE a.schedule.id = :s_id ORDER BY a.time ASC")
    Page<Appointment> getAppointmentsByScheduleId(@Param("s_id") Long s_id, PageRequest pageRequest);
}
