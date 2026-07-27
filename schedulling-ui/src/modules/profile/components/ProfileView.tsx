import React from 'react';
import Image from 'next/image';
import { ProfileResponseDTO } from '../dtos/profile.dto';

interface ProfileViewProps {
  profile: ProfileResponseDTO;
  onEdit: () => void;
}

export const ProfileView: React.FC<ProfileViewProps> = ({ profile, onEdit }) => {
  return (
    <div className="bg-white shadow rounded-lg p-6 flex flex-col items-center">
      <div className="w-32 h-32 rounded-full bg-gray-200 mb-4 overflow-hidden flex items-center justify-center border-4 border-blue-500">
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
          <span className="text-4xl text-gray-400 font-bold">{profile.name.charAt(0).toUpperCase()}</span>
        )}
      </div>
      <h2 className="text-2xl font-bold text-gray-800">{profile.name}</h2>
      <p className="text-gray-500 mb-4">{profile.email}</p>
      <div className="badge bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-xs font-semibold mb-6 uppercase">
        {profile.type === 'provider' ? 'Prestador' : 'Cliente'}
      </div>
      
      {profile.bio && (
        <div className="w-full text-center mb-6">
          <h3 className="text-sm font-semibold text-gray-700 uppercase mb-2">Sobre mim</h3>
          <p className="text-gray-600 italic">&ldquo;{profile.bio}&rdquo;</p>
        </div>
      )}
      
      <button 
        onClick={onEdit}
        className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition-colors"
      >
        Editar Perfil
      </button>
    </div>
  );
};
