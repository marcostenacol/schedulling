'use client';

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/modules/auth/store/auth.store';
import Sidebar from '@/components/ui/Sidebar';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
    }
  }, [isAuthenticated, router]);

  if (!isAuthenticated) return null;

  return (
    <div className="min-h-screen bg-app-bg flex gap-4 p-4">
      <Sidebar />
      <main className="flex-1">
        <div className="max-w-4xl mx-auto">{children}</div>
      </main>
    </div>
  );
}
