import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  isAuthenticated: boolean;
  setToken: (token: string) => void;
  logout: () => void;
}

/**
 * `isAuthenticated` é um campo simples sincronizado manualmente em cada set, não um getter
 * derivado — o merge padrão do zustand-persist na reidratação (`{...currentState, ...persistedState}`)
 * avalia getters em valor estático e congela `false` pra sempre, mesmo após um login bem-sucedido.
 */
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      isAuthenticated: false,
      setToken: (token) => {
        set({ token, isAuthenticated: true });
      },
      logout: () => {
        set({ token: null, isAuthenticated: false });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ token: state.token }),
      merge: (persistedState, currentState) => {
        const persisted = persistedState as Partial<AuthState> | undefined;
        return {
          ...currentState,
          ...persisted,
          isAuthenticated: !!persisted?.token,
        };
      },
    }
  )
);
