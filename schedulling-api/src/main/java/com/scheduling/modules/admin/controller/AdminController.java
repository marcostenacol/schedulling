package com.scheduling.modules.admin.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.admin.dto.AdminUserResponse;
import com.scheduling.modules.admin.service.ListAllUsersService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController extends BaseController {

  private final ListAllUsersService listAllUsersService;

  @GetMapping("/users")
  public ResponseEntity<ApiResponse<List<AdminUserResponse>>> listUsers() {
    List<AdminUserResponse> response =
        listAllUsersService.execute(null).stream()
            .map(AdminUserResponse::from)
            .collect(Collectors.toList());
    return success("Lista de usuários recuperada", response);
  }
}
