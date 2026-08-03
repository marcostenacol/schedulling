'use client';

import React, { useState } from 'react';
import { AxiosError } from 'axios';
import { ProfileResponseDTO, UpdateProfileDTO } from '../dtos/profile.dto';
import { ApiResponse } from '../../auth/dtos/auth.dto';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

interface ProfileFormProps {
  profile: ProfileResponseDTO;
  onSuccess: (updated: ProfileResponseDTO) => void;
  onCancel: () => void;
  onUpdate: (data: UpdateProfileDTO) => Promise<ApiResponse<ProfileResponseDTO>>;
}

export const ProfileForm: React.FC<ProfileFormProps> = ({ profile, onSuccess, onCancel, onUpdate }) => {
  const [name, setName] = useState(profile.name);
  const [bio, setBio] = useState(profile.bio || '');
  const [avatar, setAvatar] = useState(profile.avatar || '');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await onUpdate({ name, bio, avatar });
      onSuccess(response.data);
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      setError(axiosError.response?.data?.message || 'Erro ao atualizar perfil.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white shadow rounded-lg p-6 flex flex-col gap-4">
      <h2 className="text-xl font-bold text-gray-800 border-b pb-2 mb-2">Editar Informações</h2>
      
      {error && <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">{error}</div>}

      <Input 
        label="Nome Completo" 
        value={name} 
        onChange={(e) => setName(e.target.value)} 
        required 
      />
      
      <Input 
        label="URL do Avatar" 
        value={avatar} 
        onChange={(e) => setAvatar(e.target.value)} 
        placeholder="https://exemplo.com/foto.jpg"
      />

      <div className="flex flex-col gap-1 w-full">
        <label className="text-sm font-medium text-gray-700">Bio</label>
        <textarea
          className="px-3 py-2 border border-gray-300 rounded-md shadow-sm bg-white text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 h-24"
          value={bio}
          onChange={(e) => setBio(e.target.value)}
        />
      </div>

      <div className="flex gap-2 mt-2">
        <Button type="submit" isLoading={loading} className="flex-1">
          Salvar Alterações
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </form>
  );
};
