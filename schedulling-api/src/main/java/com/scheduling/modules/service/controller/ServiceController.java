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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController extends BaseController {

    private final CreateServiceService createServiceService;
    private final ListServicesService listServicesService;
    private final UpdateServiceService updateServiceService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateServiceDTO dto
    ) {
        ServiceResponseDTO response = createServiceService.execute(new CreateServiceRequest(user, dto));
        return success("Serviço criado com sucesso", response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ServiceResponseDTO>>> listMe(@AuthenticationPrincipal User user) {
        List<ServiceResponseDTO> response = listServicesService.execute(user.getId());
        return success("Serviços recuperados com sucesso", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> update(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateServiceDTO dto
    ) {
        ServiceResponseDTO response = updateServiceService.execute(new UpdateServiceRequest(id, user, dto));
        return success("Serviço atualizado com sucesso", response);
    }
}
