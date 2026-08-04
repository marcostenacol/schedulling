package com.scheduling.modules.availability.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.availability.dto.GetAvailableSlotsRequest;
import com.scheduling.modules.availability.model.Availability;
import com.scheduling.modules.availability.model.AvailabilityBlock;
import com.scheduling.modules.availability.repository.AvailabilityBlockRepository;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import com.scheduling.shared.exception.AppException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
  private final ScheduleRepository scheduleRepository;
  private final ServiceOfferedRepository serviceRepository;

  @Override
  public List<LocalTime> execute(GetAvailableSlotsRequest input) {
    ServiceOffered service =
        serviceRepository
            .findById(input.getServiceId())
            .orElseThrow(() -> new AppException("Serviço não encontrado", HttpStatus.NOT_FOUND));

    // Java DayOfWeek: 1=Segunda...7=Domingo. Modelo do domínio: 0=Domingo...6=Sábado.
    int adjustedDay =
        input.getDate().getDayOfWeek().getValue() == 7
            ? 0
            : input.getDate().getDayOfWeek().getValue();

    List<Availability> availabilities =
        availabilityRepository.findByProviderIdAndActiveTrue(input.getProviderId());

    // LinkedHashSet: um mesmo horário pode surgir tanto de uma regra recorrente quanto de uma
    // avulsa cobrindo a mesma data — sem dedupe o slot aparece repetido pro cliente.
    LinkedHashSet<LocalTime> availableSlots = new LinkedHashSet<>();
    int duration = service.getDurationMinutes();

    for (Availability avail : availabilities) {
      boolean isRecurringMatch =
          avail.getSpecificDate() == null && avail.getDayOfWeek() == adjustedDay;
      boolean isSpecificMatch = input.getDate().equals(avail.getSpecificDate());

      if (isRecurringMatch || isSpecificMatch) {
        LocalTime current = avail.getStartTime();
        while (current.plusMinutes(duration).isBefore(avail.getEndTime())
            || current.plusMinutes(duration).equals(avail.getEndTime())) {
          LocalDateTime slotStart = input.getDate().atTime(current);
          LocalDateTime slotEnd = slotStart.plusMinutes(duration);

          if (!isBlocked(input.getProviderId(), slotStart, slotEnd)
              && !isAlreadyScheduled(input.getProviderId(), slotStart, slotEnd)) {
            availableSlots.add(current);
          }

          current = current.plusMinutes(duration);
        }
      }
    }

    return new ArrayList<>(availableSlots);
  }

  private boolean isBlocked(java.util.UUID providerId, LocalDateTime start, LocalDateTime end) {
    List<AvailabilityBlock> blocks = blockRepository.findBlocksInRange(providerId, start, end);
    return !blocks.isEmpty();
  }

  private boolean isAlreadyScheduled(
      java.util.UUID providerId, LocalDateTime start, LocalDateTime end) {
    return !scheduleRepository.findOverlappingSchedules(providerId, start, end).isEmpty();
  }
}
