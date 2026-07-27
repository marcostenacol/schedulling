package com.scheduling.modules.auth.enums;

public enum RoleEnum {
  ROLE_CLIENT,
  ROLE_PROVIDER;

  /**
   * Representação amigável do papel para uso em campos de negócio (ex.: Profile.type), evitando
   * manipulação de string mágica (name().replace("ROLE_", "")) nos Services.
   */
  public String toProfileType() {
    return switch (this) {
      case ROLE_CLIENT -> "client";
      case ROLE_PROVIDER -> "provider";
    };
  }
}
