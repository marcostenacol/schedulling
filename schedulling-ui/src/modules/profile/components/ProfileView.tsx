import React, { useState } from 'react';
import Image from 'next/image';
import { Copy, Check } from 'lucide-react';
import { ProfileResponseDTO } from '../dtos/profile.dto';
import { getAvatarUrl } from '@/shared/getAvatarUrl';

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
  const avatarUrl = getAvatarUrl(profile.avatar);
  const [copied, setCopied] = useState(false);
  const isProvider = profile.type === 'provider' || profile.type === 'admin';

  const handleCopyCode = () => {
    navigator.clipboard.writeText(profile.userId);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="bg-app-surface border border-app-border shadow-app-card rounded-lg p-6 flex flex-col items-center">
      <div className="w-32 h-32 rounded-full bg-app-surface-2 mb-4 overflow-hidden flex items-center justify-center border-4 border-app-accent">
        {avatarUrl ? (
          <Image
            src={avatarUrl}
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

      {isProvider && (
        <div className="w-full mb-6">
          <h3 className="text-xs font-semibold text-app-muted uppercase mb-2 text-center">
            Código de prestador (compartilhe com clientes)
          </h3>
          <button
            onClick={handleCopyCode}
            className="w-full flex items-center justify-between gap-2 px-3 py-2 bg-app-surface-2 border border-app-border rounded-md text-app-ink text-xs font-mono hover:border-app-accent transition-colors"
          >
            <span className="truncate">{profile.userId}</span>
            {copied ? <Check className="w-4 h-4 text-app-success shrink-0" /> : <Copy className="w-4 h-4 text-app-muted shrink-0" />}
          </button>
        </div>
      )}

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
