package com.scheduling.modules.availability.repository;

import com.scheduling.modules.availability.model.AvailabilityBlock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityBlockRepository extends JpaRepository<AvailabilityBlock, UUID> {
  List<AvailabilityBlock> findByProviderIdAndEndDateTimeAfter(
      UUID providerId, LocalDateTime dateTime);

  @Query(
      "SELECT b FROM AvailabilityBlock b WHERE b.provider.id = :providerId AND "
          + "((b.startDateTime <= :end AND b.endDateTime >= :start))")
  List<AvailabilityBlock> findBlocksInRange(
      UUID providerId, LocalDateTime start, LocalDateTime end);
}
