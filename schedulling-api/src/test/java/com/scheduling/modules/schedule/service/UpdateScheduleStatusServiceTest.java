package com.scheduling.modules.schedule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.modules.schedule.dto.UpdateScheduleStatusDTO;
import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.shared.exception.AppException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateScheduleStatusServiceTest {

  @Mock private ScheduleRepository scheduleRepository;
  @Mock private ProfileRepository profileRepository;

  @InjectMocks private UpdateScheduleStatusService updateScheduleStatusService;

  private UUID scheduleId;
  private User client;
  private User provider;
  private Schedule schedule;

  @BeforeEach
  void setUp() {
    scheduleId = UUID.randomUUID();
    client = User.builder().id(UUID.randomUUID()).email("client@test.com").build();
    provider = User.builder().id(UUID.randomUUID()).email("provider@test.com").build();
    schedule =
        Schedule.builder()
            .id(scheduleId)
            .client(client)
            .provider(provider)
            .service(ServiceOffered.builder().name("Corte").build())
            .status(ScheduleStatus.PENDING)
            .build();
  }

  @Test
  @DisplayName("Prestador deve conseguir confirmar o agendamento")
  void providerShouldBeAbleToConfirm() {
    UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
    dto.setStatus(ScheduleStatus.CONFIRMED);

    when(scheduleRepository.findByIdWithRelations(scheduleId)).thenReturn(Optional.of(schedule));
    when(scheduleRepository.save(any())).thenReturn(schedule);
    when(profileRepository.findByUserId(any())).thenReturn(Optional.empty());

    var response =
        updateScheduleStatusService.execute(
            new UpdateScheduleStatusService.Input(scheduleId, provider, dto));

    assertEquals(ScheduleStatus.CONFIRMED, response.getStatus());
  }

  @Test
  @DisplayName("Cliente deve conseguir cancelar o próprio agendamento")
  void clientShouldBeAbleToCancel() {
    UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
    dto.setStatus(ScheduleStatus.CANCELLED);

    when(scheduleRepository.findByIdWithRelations(scheduleId)).thenReturn(Optional.of(schedule));
    when(scheduleRepository.save(any())).thenReturn(schedule);
    when(profileRepository.findByUserId(any())).thenReturn(Optional.empty());

    var response =
        updateScheduleStatusService.execute(
            new UpdateScheduleStatusService.Input(scheduleId, client, dto));

    assertEquals(ScheduleStatus.CANCELLED, response.getStatus());
  }

  @Test
  @DisplayName("Cliente não deve conseguir confirmar o próprio agendamento")
  void clientShouldNotBeAbleToConfirm() {
    UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
    dto.setStatus(ScheduleStatus.CONFIRMED);

    when(scheduleRepository.findByIdWithRelations(scheduleId)).thenReturn(Optional.of(schedule));

    assertThrows(
        AppException.class,
        () ->
            updateScheduleStatusService.execute(
                new UpdateScheduleStatusService.Input(scheduleId, client, dto)));
    verify(scheduleRepository, never()).save(any());
  }

  @Test
  @DisplayName("Usuário que não é cliente nem prestador não deve conseguir alterar o status")
  void unrelatedUserShouldNotBeAbleToUpdate() {
    UpdateScheduleStatusDTO dto = new UpdateScheduleStatusDTO();
    dto.setStatus(ScheduleStatus.CANCELLED);
    User stranger = User.builder().id(UUID.randomUUID()).email("stranger@test.com").build();

    when(scheduleRepository.findByIdWithRelations(scheduleId)).thenReturn(Optional.of(schedule));

    assertThrows(
        AppException.class,
        () ->
            updateScheduleStatusService.execute(
                new UpdateScheduleStatusService.Input(scheduleId, stranger, dto)));
    verify(scheduleRepository, never()).save(any());
  }
}
