import { apiClient } from '@/base/api/client';
import { ApiResponse, LoginDTO, RegisterDTO, TokenResponseDTO } from '../dtos/auth.dto';

export const authApi = {
  login: async (data: LoginDTO): Promise<ApiResponse<TokenResponseDTO>> => {
    const response = await apiClient.post<ApiResponse<TokenResponseDTO>>('/auth/login', data);
    return response.data;
  },
  register: async (data: RegisterDTO): Promise<ApiResponse<void>> => {
    const response = await apiClient.post<ApiResponse<void>>('/auth/register', data);
    return response.data;
  }
};
