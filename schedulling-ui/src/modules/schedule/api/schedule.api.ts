import { apiClient } from '@/base/api/client';
import { ApiResponse, PageResponse } from '../../auth/dtos/auth.dto';
import { CreateScheduleDTO, ScheduleResponseDTO, ScheduleStatus } from '../dtos/schedule.dto';

export const scheduleApi = {
  create: async (data: CreateScheduleDTO): Promise<ApiResponse<ScheduleResponseDTO>> => {
    const response = await apiClient.post<ApiResponse<ScheduleResponseDTO>>('/schedules', data);
    return response.data;
  },
  listMe: async (): Promise<ApiResponse<PageResponse<ScheduleResponseDTO>>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<ScheduleResponseDTO>>>('/schedules/me');
    return response.data;
  },
  updateStatus: async (id: string, status: ScheduleStatus): Promise<ApiResponse<ScheduleResponseDTO>> => {
    const response = await apiClient.patch<ApiResponse<ScheduleResponseDTO>>(`/schedules/${id}/status`, { status });
    return response.data;
  }
};
