package com.scheduling.modules.admin.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListAllUsersService implements BaseService<Pageable, Page<User>> {

  private final UserRepository repository;

  @Override
  public Page<User> execute(Pageable pageable) {
    Page<User> users = repository.findAll(pageable);
    log.info("Listagem administrativa de usuários solicitada, total={}", users.getTotalElements());
    return users;
  }
}
