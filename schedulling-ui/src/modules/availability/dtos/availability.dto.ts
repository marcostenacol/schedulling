export interface AvailabilityResponseDTO {
  id: string;
  dayOfWeek: number;
  startTime: string; // HH:mm:ss
  endTime: string;
  active: boolean;
}

export interface AvailabilityDTO {
  dayOfWeek: number;
  startTime: string;
  endTime: string;
  active: boolean;
}

export interface BlockAvailabilityDTO {
  startDateTime: string;
  endDateTime: string;
  reason?: string;
}
