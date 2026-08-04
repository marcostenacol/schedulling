package com.scheduling.modules.schedule.repository;

import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

  @Query(
      "SELECT s FROM Schedule s JOIN FETCH s.client JOIN FETCH s.provider JOIN FETCH s.service "
          + "WHERE s.id = :id")
  Optional<Schedule> findByIdWithRelations(UUID id);

  @Query(
      value =
          "SELECT s FROM Schedule s "
              + "JOIN FETCH s.client JOIN FETCH s.provider JOIN FETCH s.service "
              + "WHERE s.provider.id = :providerId AND s.status IN :statuses",
      countQuery =
          "SELECT count(s) FROM Schedule s WHERE s.provider.id = :providerId AND s.status IN :statuses")
  Page<Schedule> findByProviderIdAndStatusIn(
      UUID providerId, List<ScheduleStatus> statuses, Pageable pageable);

  @Query(
      value =
          "SELECT s FROM Schedule s "
              + "JOIN FETCH s.client JOIN FETCH s.provider JOIN FETCH s.service "
              + "WHERE s.client.id = :clientId AND s.status IN :statuses",
      countQuery =
          "SELECT count(s) FROM Schedule s WHERE s.client.id = :clientId AND s.status IN :statuses")
  Page<Schedule> findByClientIdAndStatusIn(
      UUID clientId, List<ScheduleStatus> statuses, Pageable pageable);

  /**
   * Só CONFIRMED reserva o horário de fato — PENDING não bloqueia outros clientes de solicitarem o
   * mesmo horário, evitando que uma solicitação nunca respondida trave a agenda indefinidamente.
   * Quando uma dessas solicitações concorrentes é confirmada, as demais PENDING que se sobrepõem
   * são canceladas automaticamente (ver {@link
   * com.scheduling.modules.schedule.service.UpdateScheduleStatusService}).
   */
  @Query(
      "SELECT s FROM Schedule s WHERE s.provider.id = :providerId AND "
          + "s.status = 'CONFIRMED' AND "
          + "((s.startDateTime < :end AND s.endDateTime > :start))")
  List<Schedule> findOverlappingSchedules(UUID providerId, LocalDateTime start, LocalDateTime end);

  @Query(
      "SELECT s FROM Schedule s WHERE s.provider.id = :providerId AND s.id <> :excludeId AND "
          + "s.status = 'PENDING' AND "
          + "((s.startDateTime < :end AND s.endDateTime > :start))")
  List<Schedule> findOverlappingPendingSchedulesExcluding(
      UUID providerId, LocalDateTime start, LocalDateTime end, UUID excludeId);
}
