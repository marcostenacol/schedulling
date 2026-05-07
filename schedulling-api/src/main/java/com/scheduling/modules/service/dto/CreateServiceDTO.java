package com.scheduling.modules.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateServiceDTO {

    @NotBlank(message = "O nome do serviço é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "O preço é obrigatório")
    @Min(value = 0, message = "O preço não pode ser negativo")
    private BigDecimal price;

    @NotNull(message = "A duração é obrigatória")
    @Min(value = 1, message = "A duração mínima é de 1 minuto")
    private Integer durationMinutes;
}
