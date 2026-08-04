package com.scheduling.modules.schedule.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.dto.UpdateScheduleStatusDTO;
import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import com.scheduling.shared.exception.AppException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateScheduleStatusService
    implements BaseService<UpdateScheduleStatusService.Input, ScheduleResponseDTO> {

  /** Cliente só pode cancelar a própria reserva — confirmar/concluir é ação do prestador. */
  private static final List<ScheduleStatus> CLIENT_ALLOWED_STATUSES =
      List.of(ScheduleStatus.CANCELLED);

  private final ScheduleRepository repository;
  private final ProfileRepository profileRepository;

  @Data
  @AllArgsConstructor
  public static class Input {
    private UUID scheduleId;
    private User requester;
    private UpdateScheduleStatusDTO data;
  }

  @Override
  @Transactional
  public ScheduleResponseDTO execute(Input input) {
    Schedule schedule =
        repository
            .findByIdWithRelations(input.getScheduleId())
            .orElseThrow(
                () -> new AppException("Agendamento não encontrado", HttpStatus.NOT_FOUND));

    boolean isProvider = schedule.getProvider().getId().equals(input.getRequester().getId());
    boolean isClient = schedule.getClient().getId().equals(input.getRequester().getId());

    if (!isProvider && !isClient) {
      log.warn(
          "Tentativa de alterar status do agendamento id={} por usuário sem permissão id={}",
          input.getScheduleId(),
          input.getRequester().getId());
      throw new AppException(
          "Você não tem permissão para alterar este agendamento", HttpStatus.FORBIDDEN);
    }

    ScheduleStatus newStatus = input.getData().getStatus();

    if (isClient && !isProvider && !CLIENT_ALLOWED_STATUSES.contains(newStatus)) {
      throw new AppException(
          "Como cliente, você só pode cancelar este agendamento", HttpStatus.FORBIDDEN);
    }

    schedule.setStatus(newStatus);
    Schedule updated = repository.save(schedule);

    log.info(
        "Status do agendamento id={} alterado para {} por usuário id={}",
        updated.getId(),
        newStatus,
        input.getRequester().getId());

    if (newStatus == ScheduleStatus.CONFIRMED) {
      cancelOverlappingPendingSchedules(updated);
    }

    String clientName =
        updated.getGuestName() != null
            ? updated.getGuestName()
            : profileRepository
                .findByUserId(updated.getClient().getId())
                .map(p -> p.getName())
                .orElseGet(() -> updated.getClient().getEmail());
    String providerName =
        profileRepository
            .findByUserId(updated.getProvider().getId())
            .map(p -> p.getName())
            .orElseGet(() -> updated.getProvider().getEmail());

    return ScheduleResponseDTO.builder()
        .id(updated.getId())
        .clientId(updated.getClient().getId())
        .clientName(clientName)
        .providerId(updated.getProvider().getId())
        .providerName(providerName)
        .serviceName(updated.getService().getName())
        .startDateTime(updated.getStartDateTime())
        .endDateTime(updated.getEndDateTime())
        .status(updated.getStatus())
        .price(updated.getPrice())
        .notes(updated.getNotes())
        .build();
  }

  private void cancelOverlappingPendingSchedules(Schedule confirmed) {
    List<Schedule> conflicting =
        repository.findOverlappingPendingSchedulesExcluding(
            confirmed.getProvider().getId(),
            confirmed.getStartDateTime(),
            confirmed.getEndDateTime(),
            confirmed.getId());

    conflicting.forEach(s -> s.setStatus(ScheduleStatus.CANCELLED));
    repository.saveAll(conflicting);

    conflicting.forEach(
        s ->
            log.info(
                "Agendamento id={} cancelado automaticamente por conflito com o agendamento "
                    + "confirmado id={}",
                s.getId(),
                confirmed.getId()));
  }
}
