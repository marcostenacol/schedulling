package com.scheduling.modules.admin.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAllUsersService implements BaseService<Void, List<User>> {

    private final UserRepository repository;

    @Override
    public List<User> execute(Void input) {
        return repository.findAll();
    }
}
