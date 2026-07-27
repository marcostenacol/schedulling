package com.scheduling.modules.availability.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.availability.dto.BlockAvailabilityDTO;
import com.scheduling.modules.availability.model.AvailabilityBlock;
import com.scheduling.modules.availability.repository.AvailabilityBlockRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockAvailabilityService implements BaseService<BlockAvailabilityService.Input, Void> {

    private final AvailabilityBlockRepository repository;

    @Data
    @AllArgsConstructor
    public static class Input {
        private User provider;
        private BlockAvailabilityDTO data;
    }

    @Override
    public Void execute(Input input) {
        AvailabilityBlock block = AvailabilityBlock.builder()
                .provider(input.getProvider())
                .startDateTime(input.getData().getStartDateTime())
                .endDateTime(input.getData().getEndDateTime())
                .reason(input.getData().getReason())
                .build();

        repository.save(block);
        log.info("Bloqueio de disponibilidade criado provider={}, start={}, end={}",
                input.getProvider().getId(), input.getData().getStartDateTime(), input.getData().getEndDateTime());
        return null;
    }
}
