export interface AvailabilityResponseDTO {
  id: string;
  dayOfWeek: number;
  specificDate?: string | null; // YYYY-MM-DD, presente quando é avulsa
  startTime: string; // HH:mm:ss
  endTime: string;
  active: boolean;
}

export interface AvailabilityDTO {
  dayOfWeek?: number;
  specificDate?: string;
  startTime: string;
  endTime: string;
  active: boolean;
}

export interface BlockAvailabilityDTO {
  startDateTime: string;
  endDateTime: string;
  reason?: string;
}
