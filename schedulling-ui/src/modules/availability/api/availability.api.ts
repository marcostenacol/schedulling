import { apiClient } from '@/base/api/client';
import { ApiResponse } from '../../auth/dtos/auth.dto';
import { AvailabilityDTO, AvailabilityResponseDTO, BlockAvailabilityDTO } from '../dtos/availability.dto';

export const availabilityApi = {
  set: async (data: AvailabilityDTO): Promise<ApiResponse<AvailabilityResponseDTO>> => {
    const response = await apiClient.post<ApiResponse<AvailabilityResponseDTO>>('/availability', data);
    return response.data;
  },
  listMe: async (): Promise<ApiResponse<AvailabilityResponseDTO[]>> => {
    const response = await apiClient.get<ApiResponse<AvailabilityResponseDTO[]>>('/availability/me');
    return response.data;
  },
  block: async (data: BlockAvailabilityDTO): Promise<ApiResponse<void>> => {
    const response = await apiClient.post<ApiResponse<void>>('/availability/block', data);
    return response.data;
  },
  getSlots: async (providerId: string, serviceId: string, date: string): Promise<ApiResponse<string[]>> => {
    const response = await apiClient.get<ApiResponse<string[]>>('/availability/slots', {
      params: { providerId, serviceId, date }
    });
    return response.data;
  }
};
