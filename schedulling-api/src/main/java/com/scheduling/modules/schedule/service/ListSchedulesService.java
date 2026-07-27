package com.scheduling.modules.schedule.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListSchedulesService implements BaseService<ListSchedulesService.Input, Page<ScheduleResponseDTO>> {

    private final ScheduleRepository repository;

    @Override
    public Page<ScheduleResponseDTO> execute(Input input) {
        User user = input.user();
        Pageable pageable = input.pageable();
        Page<Schedule> schedules;
        List<ScheduleStatus> statuses = Arrays.asList(ScheduleStatus.PENDING, ScheduleStatus.CONFIRMED, ScheduleStatus.COMPLETED);

        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROVIDER"))) {
            schedules = repository.findByProviderIdAndStatusIn(user.getId(), statuses, pageable);
        } else {
            schedules = repository.findByClientIdAndStatusIn(user.getId(), statuses, pageable);
        }

        return schedules.map(s -> ScheduleResponseDTO.builder()
                .id(s.getId())
                .clientId(s.getClient().getId())
                .providerId(s.getProvider().getId())
                .serviceName(s.getService().getName())
                .startDateTime(s.getStartDateTime())
                .endDateTime(s.getEndDateTime())
                .status(s.getStatus())
                .price(s.getPrice())
                .build());
    }

    public record Input(User user, Pageable pageable) {
    }
}
