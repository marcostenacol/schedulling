import React from 'react';
import { ServiceResponseDTO } from '../dtos/service.dto';

interface ServiceCardProps {
  service: ServiceResponseDTO;
  onEdit: () => void;
}

export const ServiceCard: React.FC<ServiceCardProps> = ({ service, onEdit }) => {
  return (
    <div className="bg-white border border-gray-200 rounded-xl p-5 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
      <div>
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-bold text-gray-800">{service.name}</h3>
          <span className="text-blue-600 font-bold">R$ {service.price.toFixed(2)}</span>
        </div>
        <p className="text-gray-500 text-sm mb-4 line-clamp-2">{service.description || 'Sem descrição.'}</p>
        <div className="flex items-center gap-2 text-xs text-gray-400">
          <span className="flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            {service.durationMinutes} min
          </span>
        </div>
      </div>
      <button 
        onClick={onEdit}
        className="mt-6 text-sm font-semibold text-blue-600 hover:text-blue-800 flex items-center gap-1 transition-colors"
      >
        Editar serviço
        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  );
};
