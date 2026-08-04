package com.scheduling.modules.schedule.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateScheduleDTO {

  @NotNull(message = "O prestador é obrigatório")
  private UUID providerId;

  @NotNull(message = "O serviço é obrigatório")
  private UUID serviceId;

  @NotNull(message = "A data e hora de início são obrigatórias")
  private LocalDateTime startDateTime;

  /** Nome do cliente sem conta (ex.: prestador agendando um walk-in) — sobrepõe o nome do perfil. */
  private String guestName;

  private String notes;

  /** Sobrepõe a duração padrão do serviço (em minutos) para este agendamento específico. */
  @Min(value = 1, message = "A duração mínima é de 1 minuto")
  private Integer durationMinutes;
}
