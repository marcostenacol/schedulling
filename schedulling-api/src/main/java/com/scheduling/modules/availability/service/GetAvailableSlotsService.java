package com.scheduling.modules.availability.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.availability.dto.GetAvailableSlotsRequest;
import com.scheduling.modules.availability.model.Availability;
import com.scheduling.modules.availability.model.AvailabilityBlock;
import com.scheduling.modules.availability.repository.AvailabilityBlockRepository;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import com.scheduling.shared.exception.AppException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAvailableSlotsService
    implements BaseService<GetAvailableSlotsRequest, List<LocalTime>> {

  private final AvailabilityRepository availabilityRepository;
  private final AvailabilityBlockRepository blockRepository;
  private final ServiceOfferedRepository serviceRepository;

  @Override
  public List<LocalTime> execute(GetAvailableSlotsRequest input) {
    ServiceOffered service =
        serviceRepository
            .findById(input.getServiceId())
            .orElseThrow(() -> new AppException("Serviço não encontrado", HttpStatus.NOT_FOUND));

    int dayOfWeek = input.getDate().getDayOfWeek().getValue() % 7; // 0=Sunday
    // Note: Java DayOfWeek 1=Monday...7=Sunday. My model 0=Sunday, 1=Monday...
    // Let's adjust to model: Sunday=0, Monday=1...Saturday=6
    // LocalDate.getDayOfWeek().getValue() returns 1 (Mon) to 7 (Sun).
    int adjustedDay =
        input.getDate().getDayOfWeek().getValue() == 7
            ? 0
            : input.getDate().getDayOfWeek().getValue();

    List<Availability> availabilities =
        availabilityRepository.findByProviderIdAndActiveTrue(input.getProviderId());

    List<LocalTime> availableSlots = new ArrayList<>();
    int duration = service.getDurationMinutes();

    for (Availability avail : availabilities) {
      if (avail.getDayOfWeek() == adjustedDay) {
        LocalTime current = avail.getStartTime();
        while (current.plusMinutes(duration).isBefore(avail.getEndTime())
            || current.plusMinutes(duration).equals(avail.getEndTime())) {
          LocalDateTime slotStart = input.getDate().atTime(current);
          LocalDateTime slotEnd = slotStart.plusMinutes(duration);

          if (!isBlocked(input.getProviderId(), slotStart, slotEnd)) {
            availableSlots.add(current);
          }

          current = current.plusMinutes(duration);
        }
      }
    }

    return availableSlots;
  }

  private boolean isBlocked(java.util.UUID providerId, LocalDateTime start, LocalDateTime end) {
    List<AvailabilityBlock> blocks = blockRepository.findBlocksInRange(providerId, start, end);
    return !blocks.isEmpty();
  }
}
