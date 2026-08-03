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
  },
  uploadAvatar: async (file: File): Promise<ApiResponse<ProfileResponseDTO>> => {
    const formData = new FormData();
    formData.append('avatar', file);
    const response = await apiClient.post<ApiResponse<ProfileResponseDTO>>('/profile/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  }
};
