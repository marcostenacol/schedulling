package com.scheduling.modules.availability.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class AvailabilityDTO {

    @NotNull(message = "O dia da semana é obrigatório")
    @Min(0) @Max(6)
    private Integer dayOfWeek;

    @NotNull(message = "O horário de início é obrigatório")
    private LocalTime startTime;

    @NotNull(message = "O horário de término é obrigatório")
    private LocalTime endTime;

    private boolean active = true;
}
