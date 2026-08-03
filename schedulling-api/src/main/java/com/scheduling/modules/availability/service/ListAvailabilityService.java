package com.scheduling.modules.availability.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.availability.dto.AvailabilityResponseDTO;
import com.scheduling.modules.availability.model.Availability;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListAvailabilityService implements BaseService<UUID, List<AvailabilityResponseDTO>> {

  private final AvailabilityRepository repository;

  @Override
  @Cacheable(value = "availability", key = "#providerId")
  public List<AvailabilityResponseDTO> execute(UUID providerId) {
    List<Availability> availabilities = repository.findByProviderId(providerId);

    return availabilities.stream()
        .map(
            a ->
                AvailabilityResponseDTO.builder()
                    .id(a.getId())
                    .dayOfWeek(a.getDayOfWeek())
                    .specificDate(a.getSpecificDate())
                    .startTime(a.getStartTime())
                    .endTime(a.getEndTime())
                    .active(a.isActive())
                    .build())
        .collect(Collectors.toList());
  }
}
