export interface ProfileResponseDTO {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  bio?: string;
  type: string;
}

export interface UpdateProfileDTO {
  name: string;
  avatar?: string;
  bio?: string;
}
