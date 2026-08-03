import React from 'react';
import { Clock, ArrowRight, Trash2 } from 'lucide-react';
import { ServiceResponseDTO } from '../dtos/service.dto';

interface ServiceCardProps {
  service: ServiceResponseDTO;
  onEdit: () => void;
  onDelete: () => void;
}

export const ServiceCard: React.FC<ServiceCardProps> = ({ service, onEdit, onDelete }) => {
  return (
    <div className="bg-app-surface border border-app-border rounded-xl p-5 shadow-app-card hover:shadow-lg transition-shadow flex flex-col justify-between">
      <div>
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-bold text-app-ink">{service.name}</h3>
          <span className="text-app-accent font-bold">R$ {service.price.toFixed(2)}</span>
        </div>
        <p className="text-app-muted text-sm mb-4 line-clamp-2">{service.description || 'Sem descrição.'}</p>
        <div className="flex items-center gap-2 text-xs text-app-muted">
          <span className="flex items-center gap-1">
            <Clock className="h-3 w-3" />
            {service.durationMinutes} min
          </span>
        </div>
      </div>
      <div className="mt-6 flex items-center justify-between">
        <button
          onClick={onEdit}
          className="text-sm font-semibold text-app-accent hover:opacity-80 flex items-center gap-1 transition-colors"
        >
          Editar serviço
          <ArrowRight className="h-4 w-4" />
        </button>
        <button
          onClick={onDelete}
          className="text-app-muted hover:text-app-danger transition-colors p-1"
          title="Excluir serviço"
        >
          <Trash2 className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
};
