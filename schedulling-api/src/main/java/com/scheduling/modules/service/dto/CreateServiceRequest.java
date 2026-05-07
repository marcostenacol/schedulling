package com.scheduling.modules.service.dto;

import com.scheduling.modules.auth.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateServiceRequest {
    private User provider;
    private CreateServiceDTO data;
}
