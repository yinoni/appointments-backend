package com.example.appointments_app.repo;

import com.example.appointments_app.model.business.Business;
import com.example.appointments_app.model.user.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    User saveAndFlush(User user);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByPhoneNumber(String phoneNumber);

    @Query("SELECT b FROM User u JOIN u.savedBusinesses b WHERE u.id = :userId")
    Page<Business> findSavedBusinessesByUserId(@Param("userId") Long userId, PageRequest pageRequest);
}
