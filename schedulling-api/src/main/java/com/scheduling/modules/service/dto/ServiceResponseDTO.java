package com.scheduling.modules.service.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {
  private UUID id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer durationMinutes;
  private boolean active;
  private UUID providerId;
  private String providerName;
}
