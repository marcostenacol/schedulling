package com.scheduling.modules.service.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
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
public class DeleteServiceService implements BaseService<DeleteServiceService.Input, Void> {

  private final ServiceOfferedRepository repository;

  @Data
  @AllArgsConstructor
  public static class Input {
    private UUID serviceId;
    private User provider;
  }

  /**
   * Exclusão lógica (active=false), não remoção da linha — agendamentos já criados referenciam o
   * serviço por FK, então um hard delete quebraria o histórico de agendamentos passados.
   */
  @Override
  @CacheEvict(value = "services", key = "#input.provider.id")
  public Void execute(Input input) {
    ServiceOffered service =
        repository
            .findById(input.getServiceId())
            .orElseThrow(() -> new AppException("Serviço não encontrado", HttpStatus.NOT_FOUND));

    if (!service.getProvider().getId().equals(input.getProvider().getId())) {
      log.warn(
          "Tentativa de exclusão de serviço id={} por usuário sem permissão id={}",
          input.getServiceId(),
          input.getProvider().getId());
      throw new AppException(
          "Você não tem permissão para excluir este serviço", HttpStatus.FORBIDDEN);
    }

    service.setActive(false);
    repository.save(service);

    log.info("Serviço excluído (soft-delete) id={}", service.getId());

    return null;
  }
}
