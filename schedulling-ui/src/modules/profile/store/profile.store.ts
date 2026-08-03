import { create } from 'zustand';
import { ProfileResponseDTO } from '../dtos/profile.dto';

interface ProfileState {
  profile: ProfileResponseDTO | null;
  setProfile: (profile: ProfileResponseDTO) => void;
  clear: () => void;
}

export const useProfileStore = create<ProfileState>((set) => ({
  profile: null,
  setProfile: (profile) => set({ profile }),
  clear: () => set({ profile: null }),
}));
