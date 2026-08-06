'use client';

import { RegisterForm } from '@/modules/auth/components/RegisterForm';
import Link from 'next/link';
import { useLocale } from '@/i18n/LocaleContext';

export default function RegisterPage() {
  const { t } = useLocale();

  return (
    <div className="flex flex-col items-center w-full">
      <RegisterForm />
      <div className="mt-4 text-sm text-app-muted">
        {t.auth.hasAccountQuestion}{' '}
        <Link href="/login" className="text-app-accent hover:opacity-80 font-medium">
          {t.auth.loginHereLink}
        </Link>
      </div>
    </div>
  );
}
