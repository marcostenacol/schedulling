'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { ChevronDown, Sun, Moon, LogOut, Languages } from 'lucide-react';
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
    <aside className="bg-app-surface border border-app-border rounded-2xl shadow-app-card p-3 md:p-4 w-full md:w-52 md:shrink-0 md:h-[calc(100vh-2rem)] md:sticky md:top-4 flex flex-col gap-3">
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

      <div className="hidden md:block flex-1" />

      <div className="flex flex-col gap-2 pt-3 border-t border-app-border">
        <div className="relative">
          <button
            onClick={() => setLangMenuOpen((prev) => !prev)}
            className="w-full flex items-center justify-between gap-2 rounded-lg border border-app-border px-3 py-2 text-sm font-semibold text-app-muted hover:bg-app-surface-2 transition-colors"
          >
            <span className="flex items-center gap-2">
              <Languages className="w-4 h-4" />
              <span className="uppercase">{locale}</span>
            </span>
            <ChevronDown className={`w-4 h-4 transition-transform ${langMenuOpen ? 'rotate-180' : ''}`} />
          </button>
          {langMenuOpen && (
            <div className="absolute z-10 bottom-full mb-1 md:bottom-auto md:top-full md:mb-0 md:mt-1 left-0 w-full bg-app-surface border border-app-border rounded-lg shadow-app-card overflow-hidden">
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
          className="w-full flex items-center gap-2 rounded-lg border border-app-border px-3 py-2 text-sm font-semibold text-app-muted hover:bg-app-surface-2 transition-colors"
        >
          {theme === 'dark' ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          {theme === 'dark' ? t.nav.themeLight : t.nav.themeDark}
        </button>

        <button
          onClick={() => { logout(); router.push('/login'); }}
          className="w-full flex items-center gap-2 rounded-lg border border-app-border px-3 py-2 text-sm font-semibold text-app-muted hover:text-app-danger hover:border-app-danger/40 transition-colors"
        >
          <LogOut className="w-4 h-4" />
          {t.nav.signOut}
        </button>
      </div>
    </aside>
  );
}
