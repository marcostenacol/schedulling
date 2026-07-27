package com.scheduling.modules.availability.dto;

import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDTO {
  private UUID id;
  private Integer dayOfWeek;
  private LocalTime startTime;
  private LocalTime endTime;
  private boolean active;
}
