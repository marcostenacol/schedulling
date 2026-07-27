package com.scheduling.modules.service.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class UpdateServiceDTO {
  private String name;
  private String description;
  private BigDecimal price;
  private Integer durationMinutes;
  private Boolean active;
}
