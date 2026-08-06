import React from 'react';
import { LocaleSwitcher } from '@/components/ui/LocaleSwitcher';

export default function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-app-bg py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8 bg-app-surface border border-app-border p-8 rounded-xl shadow-app-card">
        <LocaleSwitcher />
        {children}
      </div>
    </div>
  );
}
