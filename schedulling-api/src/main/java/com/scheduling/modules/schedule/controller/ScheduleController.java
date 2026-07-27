package com.scheduling.modules.schedule.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.schedule.dto.CreateScheduleDTO;
import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.service.CreateScheduleService;
import com.scheduling.modules.schedule.service.ListSchedulesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController extends BaseController {

    private final CreateScheduleService createScheduleService;
    private final ListSchedulesService listSchedulesService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponseDTO>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateScheduleDTO dto
    ) {
        ScheduleResponseDTO response = createScheduleService.execute(new CreateScheduleService.Input(user, dto));
        return success("Agendamento realizado com sucesso", response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ScheduleResponseDTO>>> listMe(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ScheduleResponseDTO> response = listSchedulesService.execute(new ListSchedulesService.Input(user, pageable));
        return success("Agendamentos recuperados com sucesso", response);
    }
}
