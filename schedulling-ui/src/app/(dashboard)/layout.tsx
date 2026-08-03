'use client';

import React, { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/modules/auth/store/auth.store';
import { useProfileStore } from '@/modules/profile/store/profile.store';
import { profileApi } from '@/modules/profile/api/profile.api';
import Sidebar from '@/components/ui/Sidebar';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, hasHydrated } = useAuthStore();
  const setProfile = useProfileStore((state) => state.setProfile);
  const router = useRouter();

  useEffect(() => {
    if (hasHydrated && !isAuthenticated) {
      router.push('/login');
    }
  }, [hasHydrated, isAuthenticated, router]);

  useEffect(() => {
    if (isAuthenticated) {
      profileApi.getMe().then((res) => setProfile(res.data)).catch(() => {});
    }
  }, [isAuthenticated, setProfile]);

  if (!hasHydrated || !isAuthenticated) return null;

  return (
    <div className="min-h-svh bg-app-bg flex flex-col md:flex-row gap-4 p-4">
      <Sidebar />
      <main className="flex-1 min-w-0">
        <div className="max-w-4xl mx-auto">{children}</div>
      </main>
    </div>
  );
}
