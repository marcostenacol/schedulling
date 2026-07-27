package com.scheduling.modules.availability.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.availability.dto.AvailabilityDTO;
import com.scheduling.modules.availability.dto.AvailabilityResponseDTO;
import com.scheduling.modules.availability.dto.BlockAvailabilityDTO;
import com.scheduling.modules.availability.dto.GetAvailableSlotsRequest;
import com.scheduling.modules.availability.service.BlockAvailabilityService;
import com.scheduling.modules.availability.service.GetAvailableSlotsService;
import com.scheduling.modules.availability.service.ListAvailabilityService;
import com.scheduling.modules.availability.service.SetAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class AvailabilityController extends BaseController {

    private final SetAvailabilityService setAvailabilityService;
    private final ListAvailabilityService listAvailabilityService;
    private final BlockAvailabilityService blockAvailabilityService;
    private final GetAvailableSlotsService getAvailableSlotsService;

    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<AvailabilityResponseDTO>> set(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AvailabilityDTO dto
    ) {
        AvailabilityResponseDTO response = setAvailabilityService.execute(new SetAvailabilityService.Input(user, dto));
        return success("Disponibilidade configurada", response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<AvailabilityResponseDTO>>> listMe(@AuthenticationPrincipal User user) {
        List<AvailabilityResponseDTO> response = listAvailabilityService.execute(user.getId());
        return success("Disponibilidades recuperadas", response);
    }

    @PostMapping("/block")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<Void>> block(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BlockAvailabilityDTO dto
    ) {
        blockAvailabilityService.execute(new BlockAvailabilityService.Input(user, dto));
        return success("Horário bloqueado com sucesso", null);
    }

    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<List<LocalTime>>> getSlots(
            @RequestParam UUID providerId,
            @RequestParam UUID serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<LocalTime> response = getAvailableSlotsService.execute(new GetAvailableSlotsRequest(providerId, serviceId, date));
        return success("Slots disponíveis recuperados", response);
    }
}
