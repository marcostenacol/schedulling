import { apiClient } from '@/base/api/client';
import { ApiResponse } from '../../auth/dtos/auth.dto';
import { CreateScheduleDTO, ScheduleResponseDTO } from '../dtos/schedule.dto';

export const scheduleApi = {
  create: async (data: CreateScheduleDTO): Promise<ApiResponse<ScheduleResponseDTO>> => {
    const response = await apiClient.post<ApiResponse<ScheduleResponseDTO>>('/schedules', data);
    return response.data;
  },
  listMe: async (): Promise<ApiResponse<ScheduleResponseDTO[]>> => {
    const response = await apiClient.get<ApiResponse<ScheduleResponseDTO[]>>('/schedules/me');
    return response.data;
  }
};
