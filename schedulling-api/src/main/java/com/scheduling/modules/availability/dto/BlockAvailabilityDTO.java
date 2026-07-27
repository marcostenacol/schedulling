package com.scheduling.modules.availability.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BlockAvailabilityDTO {
  @NotNull(message = "Início do bloqueio é obrigatório")
  private LocalDateTime startDateTime;

  @NotNull(message = "Término do bloqueio é obrigatório")
  private LocalDateTime endDateTime;

  private String reason;
}
