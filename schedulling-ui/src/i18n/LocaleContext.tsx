'use client';

import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from 'react';
import { pt, type Dictionary } from './locales/pt';
import { en } from './locales/en';
import { es } from './locales/es';

const LOCALE_STORAGE_KEY = 'locale';

export const SUPPORTED_LOCALES = ['pt', 'en', 'es'] as const;
export type Locale = (typeof SUPPORTED_LOCALES)[number];

const DICTIONARIES: Record<Locale, Dictionary> = { pt, en, es };

interface LocaleContextValue {
  locale: Locale;
  setLocale: (value: Locale) => void;
  t: Dictionary;
}

const LocaleContext = createContext<LocaleContextValue | undefined>(undefined);

export function LocaleProvider({ children }: { children: ReactNode }) {
  const [locale, setLocaleState] = useState<Locale>('pt');

  useEffect(() => {
    const stored = localStorage.getItem(LOCALE_STORAGE_KEY) as Locale | null;
    if (stored && SUPPORTED_LOCALES.includes(stored)) {
      setLocaleState(stored);
    }
  }, []);

  const setLocale = useCallback((value: Locale) => {
    setLocaleState(value);
    localStorage.setItem(LOCALE_STORAGE_KEY, value);
  }, []);

  return (
    <LocaleContext.Provider value={{ locale, setLocale, t: DICTIONARIES[locale] }}>
      {children}
    </LocaleContext.Provider>
  );
}

export function useLocale(): LocaleContextValue {
  const context = useContext(LocaleContext);
  if (!context) {
    throw new Error('useLocale deve ser usado dentro de LocaleProvider');
  }
  return context;
}
