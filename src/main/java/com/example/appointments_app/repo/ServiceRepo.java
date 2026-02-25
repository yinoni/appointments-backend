package com.example.appointments_app.repo;

import com.example.appointments_app.model.service.Service;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepo extends CrudRepository<Service, Long> {

}
