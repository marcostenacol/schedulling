package com.scheduling.modules.availability.repository;

import com.scheduling.modules.availability.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    List<Availability> findByProviderId(UUID providerId);
    List<Availability> findByProviderIdAndActiveTrue(UUID providerId);
}
