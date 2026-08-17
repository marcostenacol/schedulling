package com.scheduling.modules.admin.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.admin.dto.AdminUserResponse;
import com.scheduling.modules.admin.service.ListAllUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
  public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> listUsers(
      @PageableDefault(size = 20) Pageable pageable) {
    Page<AdminUserResponse> response =
        listAllUsersService.execute(pageable).map(AdminUserResponse::from);
    return success("Lista de usuários recuperada", response);
  }
}
