import { apiClient } from '@/base/api/client';
import { ApiResponse, PageResponse } from '../../auth/dtos/auth.dto';
import { CreateServiceDTO, ServiceResponseDTO, UpdateServiceDTO } from '../dtos/service.dto';

export const serviceApi = {
  create: async (data: CreateServiceDTO): Promise<ApiResponse<ServiceResponseDTO>> => {
    const response = await apiClient.post<ApiResponse<ServiceResponseDTO>>('/services', data);
    return response.data;
  },
  listMe: async (): Promise<ApiResponse<PageResponse<ServiceResponseDTO>>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<ServiceResponseDTO>>>('/services/me');
    return response.data;
  },
  update: async (id: string, data: UpdateServiceDTO): Promise<ApiResponse<ServiceResponseDTO>> => {
    const response = await apiClient.put<ApiResponse<ServiceResponseDTO>>(`/services/${id}`, data);
    return response.data;
  },
  delete: async (id: string): Promise<ApiResponse<void>> => {
    const response = await apiClient.delete<ApiResponse<void>>(`/services/${id}`);
    return response.data;
  },
  /** Lista os serviços de UM prestador específico — não existe um catálogo aberto de todos. */
  listByProvider: async (providerCode: string): Promise<ApiResponse<PageResponse<ServiceResponseDTO>>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<ServiceResponseDTO>>>('/services', {
      params: { providerCode, size: 100 },
    });
    return response.data;
  }
};
