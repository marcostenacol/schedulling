package com.scheduling.modules.service.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.service.dto.CreateServiceDTO;
import com.scheduling.modules.service.dto.CreateServiceRequest;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.dto.UpdateServiceDTO;
import com.scheduling.modules.service.dto.UpdateServiceRequest;
import com.scheduling.modules.service.service.CreateServiceService;
import com.scheduling.modules.service.service.ListServicesService;
import com.scheduling.modules.service.service.UpdateServiceService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController extends BaseController {

  private final CreateServiceService createServiceService;
  private final ListServicesService listServicesService;
  private final UpdateServiceService updateServiceService;

  @PostMapping
  @PreAuthorize("hasRole('PROVIDER')")
  public ResponseEntity<ApiResponse<ServiceResponseDTO>> create(
      @AuthenticationPrincipal User user, @Valid @RequestBody CreateServiceDTO dto) {
    ServiceResponseDTO response = createServiceService.execute(new CreateServiceRequest(user, dto));
    return success("Serviço criado com sucesso", response);
  }

  @GetMapping("/me")
  @PreAuthorize("hasRole('PROVIDER')")
  public ResponseEntity<ApiResponse<Page<ServiceResponseDTO>>> listMe(
      @AuthenticationPrincipal User user, @PageableDefault(size = 20) Pageable pageable) {
    Page<ServiceResponseDTO> response =
        listServicesService.execute(new ListServicesService.Input(user.getId(), pageable));
    return success("Serviços recuperados com sucesso", response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('PROVIDER')")
  public ResponseEntity<ApiResponse<ServiceResponseDTO>> update(
      @PathVariable UUID id,
      @AuthenticationPrincipal User user,
      @Valid @RequestBody UpdateServiceDTO dto) {
    ServiceResponseDTO response =
        updateServiceService.execute(new UpdateServiceRequest(id, user, dto));
    return success("Serviço atualizado com sucesso", response);
  }
}
