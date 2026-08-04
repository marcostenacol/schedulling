package com.scheduling.modules.availability.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class AvailabilityDTO {

  /**
   * Obrigatório quando {@code specificDate} não é informado (disponibilidade recorrente semanal).
   * Ignorado (recalculado a partir de {@code specificDate}) quando esta é avulsa.
   */
  @Min(0)
  @Max(6)
  private Integer dayOfWeek;

  /** Quando informado, esta disponibilidade vale só nesta data — não recorrente. */
  private LocalDate specificDate;

  @NotNull(message = "O horário de início é obrigatório")
  private LocalTime startTime;

  @NotNull(message = "O horário de término é obrigatório")
  private LocalTime endTime;

  private boolean active = true;
}
