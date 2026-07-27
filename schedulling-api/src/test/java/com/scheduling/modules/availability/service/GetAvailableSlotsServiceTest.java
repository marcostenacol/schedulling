package com.scheduling.modules.availability.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.scheduling.modules.availability.dto.GetAvailableSlotsRequest;
import com.scheduling.modules.availability.model.Availability;
import com.scheduling.modules.availability.repository.AvailabilityBlockRepository;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
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
class GetAvailableSlotsServiceTest {

  @Mock private AvailabilityRepository availabilityRepository;
  @Mock private AvailabilityBlockRepository blockRepository;
  @Mock private ServiceOfferedRepository serviceRepository;

  @InjectMocks private GetAvailableSlotsService getAvailableSlotsService;

  private UUID providerId;
  private UUID serviceId;
  private ServiceOffered service;

  @BeforeEach
  void setUp() {
    providerId = UUID.randomUUID();
    serviceId = UUID.randomUUID();
    service =
        ServiceOffered.builder()
            .id(serviceId)
            .durationMinutes(30)
            .price(new BigDecimal("50"))
            .build();
  }

  @Test
  @DisplayName("Deve gerar slots disponíveis corretamente")
  void shouldGenerateAvailableSlots() {
    LocalDate date = LocalDate.of(2024, 5, 20); // Segunda-feira (1 no model)
    Availability avail =
        Availability.builder()
            .dayOfWeek(1)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .active(true)
            .build();

    when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
    when(availabilityRepository.findByProviderIdAndActiveTrue(providerId))
        .thenReturn(List.of(avail));
    when(blockRepository.findBlocksInRange(any(), any(), any()))
        .thenReturn(Collections.emptyList());

    List<LocalTime> slots =
        getAvailableSlotsService.execute(new GetAvailableSlotsRequest(providerId, serviceId, date));

    assertEquals(2, slots.size()); // 09:00 e 09:30
    assertTrue(slots.contains(LocalTime.of(9, 0)));
    assertTrue(slots.contains(LocalTime.of(9, 30)));
  }
}
