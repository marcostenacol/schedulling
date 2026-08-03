'use client';

import React, { useState } from 'react';
import { AxiosError } from 'axios';
import { useRouter } from 'next/navigation';
import { authApi } from '../api/auth.api';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

export const RegisterForm = () => {
  const router = useRouter();
  
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<'ROLE_CLIENT' | 'ROLE_PROVIDER'>('ROLE_CLIENT');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authApi.register({ email, password, role });
      if (response.success) {
        alert('Conta criada com sucesso! Faça login.');
        router.push('/login');
      }
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      setError(axiosError.response?.data?.message || 'Erro ao registrar.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4 w-full max-w-sm">
      <h2 className="text-2xl font-bold text-center text-app-ink">Criar nova conta</h2>
      
      {error && <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">{error}</div>}

      <Input 
        type="email" 
        label="E-mail" 
        value={email} 
        onChange={(e) => setEmail(e.target.value)} 
        required 
      />
      <Input 
        type="password" 
        label="Senha" 
        value={password} 
        onChange={(e) => setPassword(e.target.value)} 
        required 
      />

      <div className="flex flex-col gap-1 w-full">
        <label className="text-sm font-medium text-app-muted">Tipo de conta</label>
        <select
          className="px-3 py-2 border border-app-border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent"
          value={role}
          onChange={(e) => setRole(e.target.value as 'ROLE_CLIENT' | 'ROLE_PROVIDER')}
        >
          <option value="ROLE_CLIENT">Cliente</option>
          <option value="ROLE_PROVIDER">Prestador de Serviço</option>
        </select>
      </div>
      
      <Button type="submit" isLoading={loading} className="mt-2">
        Registrar
      </Button>
    </form>
  );
};
