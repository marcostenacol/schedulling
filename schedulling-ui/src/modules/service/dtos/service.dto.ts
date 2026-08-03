export interface ServiceResponseDTO {
  id: string;
  name: string;
  description?: string;
  price: number;
  durationMinutes: number;
  active: boolean;
  providerId: string;
  providerName?: string;
}

export interface CreateServiceDTO {
  name: string;
  description?: string;
  price: number;
  durationMinutes: number;
}

export interface UpdateServiceDTO {
  name?: string;
  description?: string;
  price?: number;
  durationMinutes?: number;
  active?: boolean;
}
