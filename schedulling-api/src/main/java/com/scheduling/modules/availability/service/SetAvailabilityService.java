package com.scheduling.modules.availability.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.availability.dto.AvailabilityDTO;
import com.scheduling.modules.availability.dto.AvailabilityResponseDTO;
import com.scheduling.modules.availability.model.Availability;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import com.scheduling.shared.exception.AppException;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetAvailabilityService
    implements BaseService<SetAvailabilityService.Input, AvailabilityResponseDTO> {

  private final AvailabilityRepository repository;

  @Data
  @AllArgsConstructor
  public static class Input {
    private User provider;
    private AvailabilityDTO data;
  }

  @Override
  @CacheEvict(value = "availability", key = "#input.provider.id")
  public AvailabilityResponseDTO execute(Input input) {
    LocalDate specificDate = input.getData().getSpecificDate();
    Integer dayOfWeek = input.getData().getDayOfWeek();

    if (specificDate == null && dayOfWeek == null) {
      throw new AppException(
          "Informe o dia da semana (recorrente) ou uma data específica (avulsa)",
          HttpStatus.BAD_REQUEST);
    }

    if (specificDate != null) {
      dayOfWeek = toModelDayOfWeek(specificDate);
    }

    Availability availability =
        Availability.builder()
            .provider(input.getProvider())
            .dayOfWeek(dayOfWeek)
            .specificDate(specificDate)
            .startTime(input.getData().getStartTime())
            .endTime(input.getData().getEndTime())
            .active(input.getData().isActive())
            .build();

    Availability saved = repository.save(availability);

    log.info(
        "Disponibilidade definida id={}, provider={}, dayOfWeek={}, specificDate={}",
        saved.getId(),
        input.getProvider().getId(),
        saved.getDayOfWeek(),
        saved.getSpecificDate());

    return AvailabilityResponseDTO.builder()
        .id(saved.getId())
        .dayOfWeek(saved.getDayOfWeek())
        .specificDate(saved.getSpecificDate())
        .startTime(saved.getStartTime())
        .endTime(saved.getEndTime())
        .active(saved.isActive())
        .build();
  }

  /** Java {@code DayOfWeek}: 1=Segunda...7=Domingo. Modelo do domínio: 0=Domingo...6=Sábado. */
  private int toModelDayOfWeek(LocalDate date) {
    int javaDayOfWeek = date.getDayOfWeek().getValue();
    return javaDayOfWeek == 7 ? 0 : javaDayOfWeek;
  }
}
