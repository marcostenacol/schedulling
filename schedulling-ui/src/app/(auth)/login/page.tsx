import { LoginForm } from '@/modules/auth/components/LoginForm';
import Link from 'next/link';

export default function LoginPage() {
  return (
    <div className="flex flex-col items-center w-full">
      <LoginForm />
      <div className="mt-4 text-sm text-app-muted">
        Não tem uma conta?{' '}
        <Link href="/register" className="text-app-accent hover:opacity-80 font-medium">
          Registre-se aqui
        </Link>
      </div>
    </div>
  );
}
