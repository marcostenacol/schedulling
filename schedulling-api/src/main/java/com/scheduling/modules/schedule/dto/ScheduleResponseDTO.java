package com.scheduling.modules.schedule.dto;

import com.scheduling.modules.schedule.model.ScheduleStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDTO {
  private UUID id;
  private UUID clientId;
  private String clientName;
  private UUID providerId;
  private String providerName;
  private String serviceName;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private ScheduleStatus status;
  private BigDecimal price;
}
