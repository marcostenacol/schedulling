'use client';

import React, { useState } from 'react';
import { AxiosError } from 'axios';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '../store/auth.store';
import { authApi } from '../api/auth.api';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

export const LoginForm = () => {
  const router = useRouter();
  const setTokens = useAuthStore((state) => state.setTokens);
  
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authApi.login({ email, password });
      if (response.success && response.data.accessToken) {
        setTokens(response.data.accessToken, response.data.refreshToken);
        router.push('/schedule');
      }
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      setError(axiosError.response?.data?.message || 'Erro ao realizar login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4 w-full max-w-sm">
      <h2 className="text-2xl font-bold text-center text-app-ink">Bem-vindo de volta</h2>
      
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
      
      <Button type="submit" isLoading={loading} className="mt-2">
        Entrar
      </Button>
    </form>
  );
};
