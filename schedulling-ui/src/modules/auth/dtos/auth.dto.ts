export interface LoginDTO {
  email: string;
  password: string;
}

export interface RegisterDTO {
  email: string;
  password: string;
  role: 'ROLE_CLIENT' | 'ROLE_PROVIDER';
}

export interface TokenResponseDTO {
  accessToken: string;
  refreshToken: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// Formato padrão de resposta paginada do Spring Data (org.springframework.data.domain.Page<T>).
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
