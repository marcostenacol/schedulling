package com.scheduling.modules.service.repository;

import com.scheduling.modules.service.model.ServiceOffered;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOfferedRepository extends JpaRepository<ServiceOffered, UUID> {

  @Query(
      value =
          "SELECT s FROM ServiceOffered s JOIN FETCH s.provider WHERE s.provider.id = :providerId AND s.active = true",
      countQuery =
          "SELECT count(s) FROM ServiceOffered s WHERE s.provider.id = :providerId AND s.active = true")
  Page<ServiceOffered> findByProviderIdAndActiveTrue(UUID providerId, Pageable pageable);

  List<ServiceOffered> findByActiveTrue();

  @Query("SELECT s FROM ServiceOffered s JOIN FETCH s.provider WHERE s.id = :id")
  Optional<ServiceOffered> findById(UUID id);
}
