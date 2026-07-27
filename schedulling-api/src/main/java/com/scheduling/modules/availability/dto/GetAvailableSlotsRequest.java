package com.scheduling.modules.availability.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAvailableSlotsRequest {
  private UUID providerId;
  private UUID serviceId;
  private LocalDate date;
}
