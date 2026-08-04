package com.scheduling.modules.profile.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {
  private UUID id;
  private UUID userId;
  private String name;
  private String email;
  private String avatar;
  private String bio;
  private String type;
  private String code;
}
