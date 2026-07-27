package com.scheduling.modules.admin.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListAllUsersService implements BaseService<Void, List<User>> {

    private final UserRepository repository;

    @Override
    public List<User> execute(Void input) {
        List<User> users = repository.findAll();
        log.info("Listagem administrativa de usuários solicitada, total={}", users.size());
        return users;
    }
}
