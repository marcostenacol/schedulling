import React from 'react';
import Image from 'next/image';
import { ProfileResponseDTO } from '../dtos/profile.dto';

interface ProfileViewProps {
  profile: ProfileResponseDTO;
  onEdit: () => void;
}

const TYPE_LABELS: Record<string, string> = {
  provider: 'Prestador',
  client: 'Cliente',
  admin: 'Administrador',
};

export const ProfileView: React.FC<ProfileViewProps> = ({ profile, onEdit }) => {
  return (
    <div className="bg-app-surface border border-app-border shadow-app-card rounded-lg p-6 flex flex-col items-center">
      <div className="w-32 h-32 rounded-full bg-app-surface-2 mb-4 overflow-hidden flex items-center justify-center border-4 border-app-accent">
        {profile.avatar ? (
          <Image
            src={profile.avatar}
            alt={profile.name}
            width={128}
            height={128}
            unoptimized
            className="w-full h-full object-cover"
          />
        ) : (
          <span className="text-4xl text-app-muted font-bold">{profile.name.charAt(0).toUpperCase()}</span>
        )}
      </div>
      <h2 className="text-2xl font-bold text-app-ink">{profile.name}</h2>
      <p className="text-app-muted mb-4">{profile.email}</p>
      <div className="badge bg-app-accent-soft text-app-accent px-3 py-1 rounded-full text-xs font-semibold mb-6 uppercase">
        {TYPE_LABELS[profile.type] ?? profile.type}
      </div>

      {profile.bio && (
        <div className="w-full text-center mb-6">
          <h3 className="text-sm font-semibold text-app-muted uppercase mb-2">Sobre mim</h3>
          <p className="text-app-muted italic">&ldquo;{profile.bio}&rdquo;</p>
        </div>
      )}

      <button
        onClick={onEdit}
        className="w-full bg-app-accent text-app-accent-ink py-2 rounded-md hover:opacity-90 transition-colors"
      >
        Editar Perfil
      </button>
    </div>
  );
};
