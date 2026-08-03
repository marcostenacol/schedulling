package com.scheduling.modules.availability.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.availability.model.Availability;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import com.scheduling.shared.exception.AppException;
import java.util.UUID;
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
public class DeleteAvailabilityService
    implements BaseService<DeleteAvailabilityService.Input, Void> {

  private final AvailabilityRepository repository;

  @Data
  @AllArgsConstructor
  public static class Input {
    private UUID availabilityId;
    private User provider;
  }

  @Override
  @CacheEvict(value = "availability", key = "#input.provider.id")
  public Void execute(Input input) {
    Availability availability =
        repository
            .findById(input.getAvailabilityId())
            .orElseThrow(
                () -> new AppException("Disponibilidade não encontrada", HttpStatus.NOT_FOUND));

    if (!availability.getProvider().getId().equals(input.getProvider().getId())) {
      log.warn(
          "Tentativa de exclusão de disponibilidade id={} por usuário sem permissão id={}",
          input.getAvailabilityId(),
          input.getProvider().getId());
      throw new AppException(
          "Você não tem permissão para excluir esta disponibilidade", HttpStatus.FORBIDDEN);
    }

    repository.delete(availability);

    log.info("Disponibilidade excluída id={}", availability.getId());

    return null;
  }
}
