'use client';

import { useCallback, useEffect, useState } from 'react';

const THEME_STORAGE_KEY = 'theme';

export type Theme = 'dark' | 'light';

export function useTheme() {
  const [theme, setThemeState] = useState<Theme>('dark');

  useEffect(() => {
    const stored = localStorage.getItem(THEME_STORAGE_KEY) as Theme | null;
    setThemeState(stored === 'light' ? 'light' : 'dark');
  }, []);

  const setTheme = useCallback((value: Theme) => {
    setThemeState(value);
    document.documentElement.setAttribute('data-theme', value);
    localStorage.setItem(THEME_STORAGE_KEY, value);
  }, []);

  const toggleTheme = useCallback(() => {
    setTheme(theme === 'dark' ? 'light' : 'dark');
  }, [theme, setTheme]);

  return { theme, setTheme, toggleTheme };
}
