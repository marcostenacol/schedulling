import { apiClient } from '@/base/api/client';
import { ApiResponse } from '../../auth/dtos/auth.dto';
import { ProfileResponseDTO, UpdateProfileDTO } from '../dtos/profile.dto';

export const profileApi = {
  getMe: async (): Promise<ApiResponse<ProfileResponseDTO>> => {
    const response = await apiClient.get<ApiResponse<ProfileResponseDTO>>('/profile/me');
    return response.data;
  },
  update: async (data: UpdateProfileDTO): Promise<ApiResponse<ProfileResponseDTO>> => {
    const response = await apiClient.put<ApiResponse<ProfileResponseDTO>>('/profile', data);
    return response.data;
  }
};
