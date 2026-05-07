package com.scheduling.modules.availability.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GetAvailableSlotsRequest {
    private UUID providerId;
    private UUID serviceId;
    private LocalDate date;
}
