import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  setToken: (token: string) => void;
  logout: () => void;
}

interface AuthDerived extends AuthState {
  isAuthenticated: boolean;
}

export const useAuthStore = create<AuthDerived>()(
  persist(
    (set, get) => ({
      token: null,
      get isAuthenticated() {
        return get().token !== null;
      },
      setToken: (token) => {
        set({ token });
      },
      logout: () => {
        set({ token: null });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ token: state.token }),
    }
  )
);
