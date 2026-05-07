package com.scheduling.modules.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateProfileRequest {
    private UUID userId;
    private UpdateProfileDTO data;
}
