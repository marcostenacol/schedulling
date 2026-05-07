import { LoginForm } from '@/modules/auth/components/LoginForm';
import Link from 'next/link';

export default function LoginPage() {
  return (
    <div className="flex flex-col items-center w-full">
      <LoginForm />
      <div className="mt-4 text-sm text-gray-600">
        Não tem uma conta?{' '}
        <Link href="/register" className="text-blue-600 hover:text-blue-500 font-medium">
          Registre-se aqui
        </Link>
      </div>
    </div>
  );
}
