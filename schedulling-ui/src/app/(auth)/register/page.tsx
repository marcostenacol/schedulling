import { RegisterForm } from '@/modules/auth/components/RegisterForm';
import Link from 'next/link';

export default function RegisterPage() {
  return (
    <div className="flex flex-col items-center w-full">
      <RegisterForm />
      <div className="mt-4 text-sm text-app-muted">
        Já tem uma conta?{' '}
        <Link href="/login" className="text-app-accent hover:opacity-80 font-medium">
          Faça login aqui
        </Link>
      </div>
    </div>
  );
}
