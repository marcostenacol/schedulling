'use client';

import React from 'react';
import { Languages } from 'lucide-react';
import { useLocale, SUPPORTED_LOCALES } from '@/i18n/LocaleContext';

export function LocaleSwitcher() {
  const { locale, setLocale } = useLocale();

  return (
    <div className="flex items-center gap-1 self-end">
      <Languages className="w-4 h-4 text-app-muted" />
      {SUPPORTED_LOCALES.map((lng) => (
        <button
          key={lng}
          type="button"
          onClick={() => setLocale(lng)}
          aria-pressed={locale === lng}
          className={`px-2 py-1 rounded-md text-xs font-bold uppercase transition-colors ${
            locale === lng ? 'bg-app-accent-soft text-app-accent' : 'text-app-muted hover:bg-app-surface-2'
          }`}
        >
          {lng}
        </button>
      ))}
    </div>
  );
}
