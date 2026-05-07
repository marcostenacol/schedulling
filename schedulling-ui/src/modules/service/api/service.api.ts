import { apiClient } from '@/base/api/client';
import { ApiResponse } from '../../auth/dtos/auth.dto';
import { CreateServiceDTO, ServiceResponseDTO, UpdateServiceDTO } from '../dtos/service.dto';

export const serviceApi = {
  create: async (data: CreateServiceDTO): Promise<ApiResponse<ServiceResponseDTO>> => {
    const response = await apiClient.post<ApiResponse<ServiceResponseDTO>>('/services', data);
    return response.data;
  },
  listMe: async (): Promise<ApiResponse<ServiceResponseDTO[]>> => {
    const response = await apiClient.get<ApiResponse<ServiceResponseDTO[]>>('/services/me');
    return response.data;
  },
  update: async (id: string, data: UpdateServiceDTO): Promise<ApiResponse<ServiceResponseDTO>> => {
    const response = await apiClient.put<ApiResponse<ServiceResponseDTO>>(`/services/${id}`, data);
    return response.data;
  }
};
