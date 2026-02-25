package com.example.appointments_app.repo;

import com.example.appointments_app.model.business.Business;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessRepo extends CrudRepository<Business, Long> {

    @Query("SELECT b FROM Business b WHERE b.owner.id = :ownerId")
    List<Business> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Business b WHERE b.id = :businessId AND b.owner.id = :ownerId")
    Optional<Business> findBusinessByIdAndOwnerId(@Param("businessId") Long businessId, @Param("ownerId") Long ownerId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM businesses_services WHERE business_id = :businessId", nativeQuery = true)
    void deleteBusinessAssociations(@Param("businessId") Long businessId);
}
