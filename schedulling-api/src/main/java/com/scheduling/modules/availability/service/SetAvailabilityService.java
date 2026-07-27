package com.scheduling.modules.availability.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.availability.dto.AvailabilityDTO;
import com.scheduling.modules.availability.dto.AvailabilityResponseDTO;
import com.scheduling.modules.availability.model.Availability;
import com.scheduling.modules.availability.repository.AvailabilityRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SetAvailabilityService implements BaseService<SetAvailabilityService.Input, AvailabilityResponseDTO> {

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
        Availability availability = Availability.builder()
                .provider(input.getProvider())
                .dayOfWeek(input.getData().getDayOfWeek())
                .startTime(input.getData().getStartTime())
                .endTime(input.getData().getEndTime())
                .active(input.getData().isActive())
                .build();

        Availability saved = repository.save(availability);

        log.info("Disponibilidade definida id={}, provider={}, dayOfWeek={}",
                saved.getId(), input.getProvider().getId(), saved.getDayOfWeek());

        return AvailabilityResponseDTO.builder()
                .id(saved.getId())
                .dayOfWeek(saved.getDayOfWeek())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .active(saved.isActive())
                .build();
    }
}
