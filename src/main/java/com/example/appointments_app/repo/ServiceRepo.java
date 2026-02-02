package com.example.appointments_app.repo;

import com.example.appointments_app.model.Service;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ServiceRepo extends CrudRepository<Service, Long> {

}
