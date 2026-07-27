package com.scheduling.modules.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileDTO {

  @NotBlank(message = "O nome é obrigatório")
  private String name;

  private String avatar;

  private String bio;
}
