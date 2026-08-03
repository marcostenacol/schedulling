import axios from 'axios';
import { useAuthStore } from '@/modules/auth/store/auth.store';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: `${API_BASE_URL}/api/v1`,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshPromise: Promise<string | null> | null = null;

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = useAuthStore.getState().refreshToken;
  if (!refreshToken) return null;

  try {
    const response = await axios.post(`${API_BASE_URL}/api/v1/auth/refresh`, { refreshToken });
    const { accessToken, refreshToken: newRefreshToken } = response.data.data;
    useAuthStore.getState().setTokens(accessToken, newRefreshToken);
    return accessToken;
  } catch {
    return null;
  }
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      refreshPromise = refreshPromise || refreshAccessToken();
      const newToken = await refreshPromise;
      refreshPromise = null;

      if (newToken) {
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return apiClient(originalRequest);
      }

      useAuthStore.getState().logout();
      if (typeof window !== 'undefined') {
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  }
);
