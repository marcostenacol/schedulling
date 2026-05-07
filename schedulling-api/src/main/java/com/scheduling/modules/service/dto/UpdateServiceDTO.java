package com.scheduling.modules.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateServiceDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean active;
}
