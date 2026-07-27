package com.scheduling.modules.profile.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.dto.ProfileResponseDTO;
import com.scheduling.modules.profile.dto.UpdateProfileDTO;
import com.scheduling.modules.profile.dto.UpdateProfileRequest;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

  @Mock private ProfileRepository profileRepository;

  @InjectMocks private UpdateProfileService updateProfileService;

  private UUID userId;
  private Profile profile;
  private UpdateProfileDTO updateDTO;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    User user = User.builder().id(userId).email("test@example.com").build();
    profile =
        Profile.builder().id(UUID.randomUUID()).user(user).name("Old Name").type("client").build();

    updateDTO = new UpdateProfileDTO();
    updateDTO.setName("New Name");
    updateDTO.setBio("New Bio");
  }

  @Test
  @DisplayName("Deve atualizar o perfil com sucesso")
  void shouldUpdateProfileSuccessfully() {
    when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

    ProfileResponseDTO response =
        updateProfileService.execute(new UpdateProfileRequest(userId, updateDTO));

    assertNotNull(response);
    assertEquals("New Name", response.getName());
    assertEquals("New Bio", response.getBio());

    verify(profileRepository, times(1)).findByUserId(userId);
    verify(profileRepository, times(1)).save(profile);
  }
}
