package com.scheduling.modules.profile.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateProfileRequest {
  private UUID userId;
  private UpdateProfileDTO data;
}
