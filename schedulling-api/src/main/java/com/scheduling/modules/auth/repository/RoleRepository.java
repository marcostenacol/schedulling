package com.scheduling.modules.auth.repository;

import com.scheduling.modules.auth.enums.RoleEnum;
import com.scheduling.modules.auth.model.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
  Optional<Role> findByName(RoleEnum name);
}
