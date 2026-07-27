'use client';

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/modules/auth/store/auth.store';
import Link from 'next/link';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, logout } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated) return null;

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <nav className="bg-white shadow-sm h-16 flex items-center justify-between px-8">
        <div className="flex items-center gap-8">
          <h1 className="text-xl font-bold text-blue-600">Scheduling App</h1>
          <div className="flex gap-6">
            <Link href="/schedule" className="text-gray-600 hover:text-blue-600 font-semibold transition-colors">Agendamentos</Link>
            <Link href="/services" className="text-gray-600 hover:text-blue-600 font-semibold transition-colors">Serviços</Link>
            <Link href="/availability" className="text-gray-600 hover:text-blue-600 font-semibold transition-colors">Disponibilidade</Link>
            <Link href="/profile" className="text-gray-600 hover:text-blue-600 font-semibold transition-colors">Meu Perfil</Link>
          </div>
        </div>
        <button 
          onClick={() => { logout(); router.push('/login'); }}
          className="text-gray-500 hover:text-red-600 font-medium"
        >
          Sair
        </button>
      </nav>
      <main className="flex-1 p-8">
        <div className="max-w-4xl mx-auto">
          {children}
        </div>
      </main>
    </div>
  );
}
