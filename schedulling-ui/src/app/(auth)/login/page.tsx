'use client';

import { LoginForm } from '@/modules/auth/components/LoginForm';
import Link from 'next/link';
import { useLocale } from '@/i18n/LocaleContext';

export default function LoginPage() {
  const { t } = useLocale();

  return (
    <div className="flex flex-col items-center w-full">
      <LoginForm />
      <div className="mt-4 text-sm text-app-muted">
        {t.auth.noAccountQuestion}{' '}
        <Link href="/register" className="text-app-accent hover:opacity-80 font-medium">
          {t.auth.registerHereLink}
        </Link>
      </div>
    </div>
  );
}
