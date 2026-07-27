import { apiClient } from '@/base/api/client';
import { ApiResponse } from '../../auth/dtos/auth.dto';

export interface AdminUser {
  id: string;
  email: string;
  role?: { name: string };
  createdAt: string;
}

export const adminApi = {
  listUsers: async (): Promise<ApiResponse<AdminUser[]>> => {
    const response = await apiClient.get<ApiResponse<AdminUser[]>>('/admin/users');
    return response.data;
  }
};
