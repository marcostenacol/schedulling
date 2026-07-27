package com.scheduling.modules.admin.dto;

import com.scheduling.modules.auth.model.User;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserResponse {

  private UUID id;
  private String email;
  private String role;
  private LocalDateTime createdAt;

  public static AdminUserResponse from(User user) {
    return AdminUserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .role(user.getRole().getName().name())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
