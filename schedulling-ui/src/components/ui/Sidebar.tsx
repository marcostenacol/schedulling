'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useAuthStore } from '@/modules/auth/store/auth.store';
import { useTheme } from '@/hooks/useTheme';
import { useLocale, SUPPORTED_LOCALES } from '@/i18n/LocaleContext';

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const { logout } = useAuthStore();
  const { theme, toggleTheme } = useTheme();
  const { locale, setLocale, t } = useLocale();
  const [langMenuOpen, setLangMenuOpen] = useState(false);

  const navItems = [
    { label: t.nav.schedule, href: '/schedule' },
    { label: t.nav.services, href: '/services' },
    { label: t.nav.availability, href: '/availability' },
    { label: t.nav.profile, href: '/profile' },
  ];

  return (
    <aside className="bg-app-surface border border-app-border rounded-2xl shadow-app-card p-3 md:p-4 w-full md:w-52 md:shrink-0 flex flex-col gap-3">
      <nav className="flex flex-row md:flex-col gap-1 overflow-x-auto">
        {navItems.map((item) => {
          const isActive = pathname === item.href || pathname?.startsWith(`${item.href}/`);

          return (
            <Link
              key={item.href}
              href={item.href}
              aria-current={isActive ? 'page' : undefined}
              className={`whitespace-nowrap shrink-0 rounded-lg px-3 py-2 text-sm font-semibold transition-colors ${
                isActive ? 'bg-app-accent-soft text-app-accent' : 'text-app-muted hover:bg-app-surface-2'
              }`}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className="flex flex-row md:flex-col gap-1 pt-3 border-t border-app-border">
        <div className="relative">
          <button
            onClick={() => setLangMenuOpen((prev) => !prev)}
            className="w-full whitespace-nowrap rounded-lg px-3 py-2 text-sm font-semibold uppercase text-left text-app-muted hover:bg-app-surface-2 transition-colors"
          >
            {locale}
          </button>
          {langMenuOpen && (
            <div className="absolute z-10 top-full mt-1 left-0 w-24 bg-app-surface border border-app-border rounded-lg shadow-app-card overflow-hidden">
              {SUPPORTED_LOCALES.map((lng) => (
                <button
                  key={lng}
                  onClick={() => { setLocale(lng); setLangMenuOpen(false); }}
                  className={`w-full whitespace-nowrap px-3 py-2 text-sm font-semibold uppercase text-left transition-colors ${
                    locale === lng ? 'bg-app-accent-soft text-app-accent' : 'text-app-muted hover:bg-app-surface-2'
                  }`}
                >
                  {lng}
                </button>
              ))}
            </div>
          )}
        </div>

        <button
          onClick={toggleTheme}
          className="whitespace-nowrap rounded-lg px-3 py-2 text-sm font-semibold text-left text-app-muted hover:bg-app-surface-2 transition-colors"
        >
          {theme === 'dark' ? t.nav.themeLight : t.nav.themeDark}
        </button>

        <button
          onClick={() => { logout(); router.push('/login'); }}
          className="whitespace-nowrap rounded-lg px-3 py-2 text-sm font-semibold text-left text-app-muted hover:text-app-danger transition-colors"
        >
          {t.nav.signOut}
        </button>
      </div>
    </aside>
  );
}
