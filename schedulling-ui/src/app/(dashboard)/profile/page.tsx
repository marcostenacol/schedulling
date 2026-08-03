'use client';

import React, { useEffect, useState } from 'react';
import { profileApi } from '@/modules/profile/api/profile.api';
import { ProfileResponseDTO } from '@/modules/profile/dtos/profile.dto';
import { ProfileView } from '@/modules/profile/components/ProfileView';
import { ProfileForm } from '@/modules/profile/components/ProfileForm';
import { useProfileStore } from '@/modules/profile/store/profile.store';

export default function ProfilePage() {
  const [profile, setProfile] = useState<ProfileResponseDTO | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const setGlobalProfile = useProfileStore((state) => state.setProfile);

  useEffect(() => {
    profileApi.getMe()
      .then(res => {
        setProfile(res.data);
        setGlobalProfile(res.data);
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [setGlobalProfile]);

  const handleUpdated = (updated: ProfileResponseDTO) => {
    setProfile(updated);
    setGlobalProfile(updated);
  };

  if (loading) return <div className="flex justify-center py-20 text-app-accent font-medium">Carregando perfil...</div>;
  if (!profile) return <div className="text-red-500">Erro ao carregar dados do perfil.</div>;

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <h1 className="text-3xl font-extrabold text-app-ink tracking-tight">Configurações da Conta</h1>

      {isEditing ? (
        <ProfileForm
          profile={profile}
          onSuccess={(updated) => {
            handleUpdated(updated);
            setIsEditing(false);
          }}
          onAvatarUploaded={handleUpdated}
          onCancel={() => setIsEditing(false)}
          onUpdate={profileApi.update}
        />
      ) : (
        <ProfileView
          profile={profile}
          onEdit={() => setIsEditing(true)}
        />
      )}
    </div>
  );
}
