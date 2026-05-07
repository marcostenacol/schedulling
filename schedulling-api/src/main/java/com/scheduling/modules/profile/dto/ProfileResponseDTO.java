package com.scheduling.modules.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String avatar;
    private String bio;
    private String type;
}
