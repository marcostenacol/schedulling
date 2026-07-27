package com.scheduling.base.controller;

import com.scheduling.base.traits.ApiResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

  protected <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
    return ResponseEntity.ok(ApiResponse.success(message, data));
  }

  protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
    return success("Operação realizada com sucesso", data);
  }
}
