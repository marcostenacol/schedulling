'use client';

import React, { useRef, useState } from 'react';
import { AxiosError } from 'axios';
import { Camera } from 'lucide-react';
import { ProfileResponseDTO, UpdateProfileDTO } from '../dtos/profile.dto';
import { ApiResponse } from '../../auth/dtos/auth.dto';
import { profileApi } from '../api/profile.api';
import { getAvatarUrl } from '@/shared/getAvatarUrl';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

interface ProfileFormProps {
  profile: ProfileResponseDTO;
  onSuccess: (updated: ProfileResponseDTO) => void;
  onAvatarUploaded: (updated: ProfileResponseDTO) => void;
  onCancel: () => void;
  onUpdate: (data: UpdateProfileDTO) => Promise<ApiResponse<ProfileResponseDTO>>;
}

export const ProfileForm: React.FC<ProfileFormProps> = ({ profile, onSuccess, onAvatarUploaded, onCancel, onUpdate }) => {
  const [name, setName] = useState(profile.name);
  const [bio, setBio] = useState(profile.bio || '');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [avatarUploading, setAvatarUploading] = useState(false);
  const [avatarError, setAvatarError] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);

  const avatarUrl = getAvatarUrl(profile.avatar);

  const handleAvatarChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setAvatarUploading(true);
    setAvatarError('');

    try {
      const response = await profileApi.uploadAvatar(file);
      onAvatarUploaded(response.data);
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      setAvatarError(axiosError.response?.data?.message || 'Erro ao enviar avatar.');
    } finally {
      setAvatarUploading(false);
      e.target.value = '';
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await onUpdate({ name, bio });
      onSuccess(response.data);
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      setError(axiosError.response?.data?.message || 'Erro ao atualizar perfil.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-app-surface border border-app-border shadow-app-card rounded-lg p-6 flex flex-col gap-4">
      <h2 className="text-xl font-bold text-app-ink border-b border-app-border pb-2 mb-2">Editar Informações</h2>

      {error && <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">{error}</div>}

      <div className="flex flex-col items-center gap-2">
        <button
          type="button"
          onClick={() => fileInputRef.current?.click()}
          disabled={avatarUploading}
          className="relative w-24 h-24 rounded-full bg-app-surface-2 border-4 border-app-accent overflow-hidden flex items-center justify-center group"
        >
          {avatarUrl ? (
            // eslint-disable-next-line @next/next/no-img-element
            <img src={avatarUrl} alt={profile.name} className="w-full h-full object-cover" />
          ) : (
            <span className="text-3xl text-app-muted font-bold">{profile.name.charAt(0).toUpperCase()}</span>
          )}
          <span className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
            <Camera className="w-6 h-6 text-white" />
          </span>
        </button>
        <input
          ref={fileInputRef}
          type="file"
          accept="image/png,image/jpeg,image/webp,image/gif"
          onChange={handleAvatarChange}
          className="hidden"
        />
        <span className="text-xs text-app-muted">{avatarUploading ? 'Enviando...' : 'Clique na foto para trocar o avatar'}</span>
        {avatarError && <span className="text-xs text-red-500">{avatarError}</span>}
      </div>

      <Input
        label="Nome Completo"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
      />

      <div className="flex flex-col gap-1 w-full">
        <label className="text-sm font-medium text-app-muted">Bio</label>
        <textarea
          className="px-3 py-2 border border-app-border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent h-24"
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
