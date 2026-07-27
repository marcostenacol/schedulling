package com.scheduling.modules.profile.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.dto.ProfileResponseDTO;
import com.scheduling.modules.profile.dto.UpdateProfileDTO;
import com.scheduling.modules.profile.dto.UpdateProfileRequest;
import com.scheduling.modules.profile.service.DetailProfileService;
import com.scheduling.modules.profile.service.UpdateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController extends BaseController {

  private final DetailProfileService detailProfileService;
  private final UpdateProfileService updateProfileService;

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<ProfileResponseDTO>> me(@AuthenticationPrincipal User user) {
    ProfileResponseDTO response = detailProfileService.execute(user.getId());
    return success("Perfil recuperado com sucesso", response);
  }

  @PutMapping
  public ResponseEntity<ApiResponse<ProfileResponseDTO>> update(
      @AuthenticationPrincipal User user, @Valid @RequestBody UpdateProfileDTO updateProfileDTO) {
    ProfileResponseDTO response =
        updateProfileService.execute(new UpdateProfileRequest(user.getId(), updateProfileDTO));
    return success("Perfil atualizado com sucesso", response);
  }
}
