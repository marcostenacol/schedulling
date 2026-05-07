package com.scheduling.modules.service.dto;

import com.scheduling.modules.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateServiceRequest {
    private UUID serviceId;
    private User provider;
    private UpdateServiceDTO data;
}
