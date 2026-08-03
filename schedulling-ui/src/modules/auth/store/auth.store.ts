import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  hasHydrated: boolean;
  setTokens: (token: string, refreshToken: string) => void;
  logout: () => void;
  setHasHydrated: (value: boolean) => void;
}

/**
 * `isAuthenticated` é um campo simples sincronizado manualmente em cada set, não um getter
 * derivado — o merge padrão do zustand-persist na reidratação (`{...currentState, ...persistedState}`)
 * avalia getters em valor estático e congela `false` pra sempre, mesmo após um login bem-sucedido.
 *
 * `hasHydrated` existe porque a reidratação do zustand-persist é assíncrona mesmo usando
 * localStorage (síncrono) — sem esse flag, um `useEffect` de guarda de rota que roda no primeiro
 * render vê `isAuthenticated=false` (estado inicial) antes da reidratação terminar e redireciona
 * pro login mesmo com um token válido já salvo.
 */
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      refreshToken: null,
      isAuthenticated: false,
      hasHydrated: false,
      setTokens: (token, refreshToken) => {
        set({ token, refreshToken, isAuthenticated: true });
      },
      logout: () => {
        set({ token: null, refreshToken: null, isAuthenticated: false });
      },
      setHasHydrated: (value) => {
        set({ hasHydrated: value });
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ token: state.token, refreshToken: state.refreshToken }),
      merge: (persistedState, currentState) => {
        const persisted = persistedState as Partial<AuthState> | undefined;
        return {
          ...currentState,
          ...persisted,
          isAuthenticated: !!persisted?.token,
        };
      },
      onRehydrateStorage: () => (state) => {
        state?.setHasHydrated(true);
      },
    }
  )
);
