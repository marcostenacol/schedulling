package com.scheduling.modules.schedule.dto;

import com.scheduling.modules.schedule.model.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateScheduleStatusDTO {

  @NotNull(message = "O status é obrigatório")
  private ScheduleStatus status;
}
