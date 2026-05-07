export enum ScheduleStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

export interface ScheduleResponseDTO {
  id: string;
  clientId: string;
  clientName: string;
  providerId: string;
  providerName: string;
  serviceName: string;
  startDateTime: string;
  endDateTime: string;
  status: ScheduleStatus;
  price: number;
}

export interface CreateScheduleDTO {
  providerId: string;
  serviceId: string;
  startDateTime: string;
}
