package com.scheduling.modules.schedule.repository;

import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    
    List<Schedule> findByProviderIdAndStatusIn(UUID providerId, List<ScheduleStatus> statuses);
    List<Schedule> findByClientIdAndStatusIn(UUID clientId, List<ScheduleStatus> statuses);

    @Query("SELECT s FROM Schedule s WHERE s.provider.id = :providerId AND " +
           "s.status != 'CANCELLED' AND " +
           "((s.startDateTime < :end AND s.endDateTime > :start))")
    List<Schedule> findOverlappingSchedules(UUID providerId, LocalDateTime start, LocalDateTime end);
}
