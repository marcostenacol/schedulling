package com.scheduling.modules.service.repository;

import com.scheduling.modules.service.model.ServiceOffered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceOfferedRepository extends JpaRepository<ServiceOffered, UUID> {
    List<ServiceOffered> findByProviderIdAndActiveTrue(UUID providerId);
    List<ServiceOffered> findByActiveTrue();
}
