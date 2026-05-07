import { RegisterForm } from '@/modules/auth/components/RegisterForm';
import Link from 'next/link';

export default function RegisterPage() {
  return (
    <div className="flex flex-col items-center w-full">
      <RegisterForm />
      <div className="mt-4 text-sm text-gray-600">
        Já tem uma conta?{' '}
        <Link href="/login" className="text-blue-600 hover:text-blue-500 font-medium">
          Faça login aqui
        </Link>
      </div>
    </div>
  );
}
