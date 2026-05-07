package com.scheduling.modules.schedule.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListSchedulesService implements BaseService<User, List<ScheduleResponseDTO>> {

    private final ScheduleRepository repository;

    @Override
    public List<ScheduleResponseDTO> execute(User user) {
        List<Schedule> schedules;
        List<ScheduleStatus> statuses = Arrays.asList(ScheduleStatus.PENDING, ScheduleStatus.CONFIRMED, ScheduleStatus.COMPLETED);

        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROVIDER"))) {
            schedules = repository.findByProviderIdAndStatusIn(user.getId(), statuses);
        } else {
            schedules = repository.findByClientIdAndStatusIn(user.getId(), statuses);
        }

        return schedules.stream().map(s -> ScheduleResponseDTO.builder()
                .id(s.getId())
                .clientId(s.getClient().getId())
                .providerId(s.getProvider().getId())
                .serviceName(s.getService().getName())
                .startDateTime(s.getStartDateTime())
                .endDateTime(s.getEndDateTime())
                .status(s.getStatus())
                .price(s.getPrice())
                .build()).collect(Collectors.toList());
    }
}
