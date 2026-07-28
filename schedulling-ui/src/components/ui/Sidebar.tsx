'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useAuthStore } from '@/modules/auth/store/auth.store';

interface NavItem {
  label: string;
  href: string;
}

const NAV_ITEMS: NavItem[] = [
  { label: 'Agendamentos', href: '/schedule' },
  { label: 'Serviços', href: '/services' },
  { label: 'Disponibilidade', href: '/availability' },
  { label: 'Meu Perfil', href: '/profile' },
];

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const { logout } = useAuthStore();

  return (
    <aside className="bg-app-surface border border-app-border rounded-2xl shadow-app-card p-4 w-52 shrink-0 flex flex-col gap-1">
      <div className="flex-1 flex flex-col gap-1">
        {NAV_ITEMS.map((item) => {
          const isActive = pathname === item.href || pathname?.startsWith(`${item.href}/`);

          return (
            <Link
              key={item.href}
              href={item.href}
              aria-current={isActive ? 'page' : undefined}
              className={`rounded-lg px-3 py-2 text-sm font-semibold transition-colors ${
                isActive ? 'bg-app-accent-soft text-app-accent' : 'text-app-muted hover:bg-app-surface-2'
              }`}
            >
              {item.label}
            </Link>
          );
        })}
      </div>
      <button
        onClick={() => { logout(); router.push('/login'); }}
        className="rounded-lg px-3 py-2 text-sm font-semibold text-left text-app-muted hover:text-app-danger transition-colors"
      >
        Sair
      </button>
    </aside>
  );
}
