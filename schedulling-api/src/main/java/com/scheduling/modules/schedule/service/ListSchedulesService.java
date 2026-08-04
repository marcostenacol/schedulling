package com.scheduling.modules.schedule.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.enums.RoleEnum;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListSchedulesService
    implements BaseService<ListSchedulesService.Input, Page<ScheduleResponseDTO>> {

  private final ScheduleRepository repository;
  private final ProfileRepository profileRepository;

  @Override
  public Page<ScheduleResponseDTO> execute(Input input) {
    User user = input.user();
    Pageable pageable = input.pageable();
    Page<Schedule> schedules;
    List<ScheduleStatus> statuses =
        Arrays.asList(ScheduleStatus.PENDING, ScheduleStatus.CONFIRMED, ScheduleStatus.COMPLETED);

    if (user.getRole().getName() == RoleEnum.ROLE_PROVIDER) {
      schedules = repository.findByProviderIdAndStatusIn(user.getId(), statuses, pageable);
    } else {
      schedules = repository.findByClientIdAndStatusIn(user.getId(), statuses, pageable);
    }

    return schedules.map(this::toResponseDTO);
  }

  private ScheduleResponseDTO toResponseDTO(Schedule s) {
    String clientName =
        s.getGuestName() != null
            ? s.getGuestName()
            : profileRepository
                .findByUserId(s.getClient().getId())
                .map(p -> p.getName())
                .orElse(s.getClient().getEmail());
    String providerName =
        profileRepository
            .findByUserId(s.getProvider().getId())
            .map(p -> p.getName())
            .orElse(s.getProvider().getEmail());

    return ScheduleResponseDTO.builder()
        .id(s.getId())
        .clientId(s.getClient().getId())
        .clientName(clientName)
        .providerId(s.getProvider().getId())
        .providerName(providerName)
        .serviceName(s.getService().getName())
        .startDateTime(s.getStartDateTime())
        .endDateTime(s.getEndDateTime())
        .status(s.getStatus())
        .price(s.getPrice())
        .notes(s.getNotes())
        .build();
  }

  public record Input(User user, Pageable pageable) {}
}
