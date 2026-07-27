package com.scheduling.modules.schedule.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.UserRepository;
import com.scheduling.modules.availability.repository.AvailabilityBlockRepository;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.modules.schedule.dto.CreateScheduleDTO;
import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import com.scheduling.shared.exception.AppException;
import java.time.LocalDateTime;
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
public class CreateScheduleService
    implements BaseService<CreateScheduleService.Input, ScheduleResponseDTO> {

  private final ScheduleRepository scheduleRepository;
  private final ServiceOfferedRepository serviceRepository;
  private final UserRepository userRepository;
  private final AvailabilityRepository availabilityRepository;
  private final AvailabilityBlockRepository blockRepository;
  private final ProfileRepository profileRepository;

  @Data
  @AllArgsConstructor
  public static class Input {
    private User client;
    private CreateScheduleDTO data;
  }

  @Override
  @Transactional
  public ScheduleResponseDTO execute(Input input) {
    ServiceOffered service =
        serviceRepository
            .findById(input.getData().getServiceId())
            .orElseThrow(() -> new AppException("Serviço não encontrado", HttpStatus.NOT_FOUND));

    User provider =
        userRepository
            .findById(input.getData().getProviderId())
            .orElseThrow(() -> new AppException("Prestador não encontrado", HttpStatus.NOT_FOUND));

    LocalDateTime start = input.getData().getStartDateTime();
    LocalDateTime end = start.plusMinutes(service.getDurationMinutes());

    // 1. Validar conflitos com outros agendamentos
    if (!scheduleRepository.findOverlappingSchedules(provider.getId(), start, end).isEmpty()) {
      log.warn(
          "Conflito de horário ao criar agendamento para provider={}, start={}",
          provider.getId(),
          start);
      throw new AppException("Este horário já está ocupado", HttpStatus.CONFLICT);
    }

    // 2. Validar conflitos com blocos de indisponibilidade
    if (!blockRepository.findBlocksInRange(provider.getId(), start, end).isEmpty()) {
      log.warn(
          "Horário bloqueado ao criar agendamento para provider={}, start={}",
          provider.getId(),
          start);
      throw new AppException("O prestador não está disponível neste horário", HttpStatus.CONFLICT);
    }

    // 3. TODO: Validar se está dentro da disponibilidade semanal (pode ser feito aqui ou assumido
    // que o front já validou via getSlots)

    Schedule schedule =
        Schedule.builder()
            .client(input.getClient())
            .provider(provider)
            .service(service)
            .startDateTime(start)
            .endDateTime(end)
            .status(ScheduleStatus.PENDING)
            .price(service.getPrice())
            .build();

    Schedule saved = scheduleRepository.save(schedule);

    log.info(
        "Agendamento criado id={}, provider={}, client={}, start={}",
        saved.getId(),
        provider.getId(),
        input.getClient().getId(),
        start);

    String clientName =
        profileRepository
            .findByUserId(saved.getClient().getId())
            .map(p -> p.getName())
            .orElse(saved.getClient().getEmail());
    String providerName =
        profileRepository
            .findByUserId(saved.getProvider().getId())
            .map(p -> p.getName())
            .orElse(saved.getProvider().getEmail());

    return ScheduleResponseDTO.builder()
        .id(saved.getId())
        .clientId(saved.getClient().getId())
        .clientName(clientName)
        .providerId(saved.getProvider().getId())
        .providerName(providerName)
        .serviceName(saved.getService().getName())
        .startDateTime(saved.getStartDateTime())
        .endDateTime(saved.getEndDateTime())
        .status(saved.getStatus())
        .price(saved.getPrice())
        .build();
  }
}
