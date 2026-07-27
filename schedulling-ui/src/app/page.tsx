'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/modules/auth/store/auth.store';

export default function Home() {
  const { isAuthenticated } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated) {
      router.replace('/schedule');
    } else {
      router.replace('/login');
    }
  }, [isAuthenticated, router]);

  return null;
}
