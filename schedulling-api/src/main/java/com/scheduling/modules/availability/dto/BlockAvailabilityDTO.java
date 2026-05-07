package com.scheduling.modules.availability.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlockAvailabilityDTO {
    @NotNull(message = "Início do bloqueio é obrigatório")
    private LocalDateTime startDateTime;

    @NotNull(message = "Término do bloqueio é obrigatório")
    private LocalDateTime endDateTime;

    private String reason;
}
