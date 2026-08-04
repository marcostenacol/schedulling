export interface ProfileResponseDTO {
  id: string;
  userId: string;
  name: string;
  email: string;
  avatar?: string;
  bio?: string;
  type: string;
}

export interface UpdateProfileDTO {
  name: string;
  bio?: string;
}
