package com.scheduling.modules.service.dto;

import com.scheduling.modules.auth.model.User;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateServiceRequest {
  private UUID serviceId;
  private User provider;
  private UpdateServiceDTO data;
}
