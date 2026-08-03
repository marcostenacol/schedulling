package com.scheduling.modules.profile.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UpdateAvatarRequest {
  private UUID userId;
  private MultipartFile file;
}
