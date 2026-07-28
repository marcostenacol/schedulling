package com.scheduling.modules.schedule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CreateScheduleServiceTest {

  @Mock private ScheduleRepository scheduleRepository;
  @Mock private ServiceOfferedRepository serviceRepository;
  @Mock private UserRepository userRepository;
  @Mock private AvailabilityRepository availabilityRepository;
  @Mock private AvailabilityBlockRepository blockRepository;
  @Mock private ProfileRepository profileRepository;

  @InjectMocks private CreateScheduleService createScheduleService;

  private User client;
  private User provider;
  private ServiceOffered service;
  private CreateScheduleDTO createDTO;

  @BeforeEach
  void setUp() {
    client = User.builder().id(UUID.randomUUID()).email("client@test.com").build();
    provider = User.builder().id(UUID.randomUUID()).email("provider@test.com").build();
    service =
        ServiceOffered.builder()
            .id(UUID.randomUUID())
            .name("Serviço")
            .price(new BigDecimal("100"))
            .durationMinutes(60)
            .build();

    createDTO = new CreateScheduleDTO();
    createDTO.setProviderId(provider.getId());
    createDTO.setServiceId(service.getId());
    createDTO.setStartDateTime(LocalDateTime.of(2024, 6, 1, 10, 0));
  }

  @Test
  @DisplayName("Deve criar agendamento com sucesso")
  void shouldCreateScheduleSuccessfully() {
    when(serviceRepository.findById(createDTO.getServiceId())).thenReturn(Optional.of(service));
    when(userRepository.findById(createDTO.getProviderId())).thenReturn(Optional.of(provider));
    when(scheduleRepository.findOverlappingSchedules(any(), any(), any()))
        .thenReturn(Collections.emptyList());
    when(blockRepository.findBlocksInRange(any(), any(), any()))
        .thenReturn(Collections.emptyList());
    when(profileRepository.findByUserId(any())).thenReturn(Optional.empty());

    Schedule saved =
        Schedule.builder()
            .id(UUID.randomUUID())
            .client(client)
            .provider(provider)
            .service(service)
            .status(ScheduleStatus.PENDING)
            .startDateTime(createDTO.getStartDateTime())
            .endDateTime(createDTO.getStartDateTime().plusMinutes(60))
            .price(service.getPrice())
            .build();

    when(scheduleRepository.save(any(Schedule.class))).thenReturn(saved);

    ScheduleResponseDTO response =
        createScheduleService.execute(new CreateScheduleService.Input(client, createDTO));

    assertNotNull(response);
    assertEquals(service.getName(), response.getServiceName());
    verify(scheduleRepository, times(1)).save(any(Schedule.class));
  }

  @Test
  @DisplayName("Deve falhar se houver conflito de horário")
  void shouldFailWhenConflictExists() {
    when(serviceRepository.findById(createDTO.getServiceId())).thenReturn(Optional.of(service));
    when(userRepository.findById(createDTO.getProviderId())).thenReturn(Optional.of(provider));
    when(scheduleRepository.findOverlappingSchedules(any(), any(), any()))
        .thenReturn(Collections.singletonList(new Schedule()));

    AppException exception =
        assertThrows(
            AppException.class,
            () ->
                createScheduleService.execute(new CreateScheduleService.Input(client, createDTO)));

    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("Este horário já está ocupado", exception.getMessage());
  }
}
