'use client';

import React, { useState } from 'react';
import { CreateServiceDTO, ServiceResponseDTO, UpdateServiceDTO } from '../dtos/service.dto';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';

interface ServiceFormProps {
  service?: ServiceResponseDTO;
  onSuccess: () => void;
  onCancel: () => void;
  onSubmit: (data: any) => Promise<any>;
}

export const ServiceForm: React.FC<ServiceFormProps> = ({ service, onSuccess, onCancel, onSubmit }) => {
  const [name, setName] = useState(service?.name || '');
  const [description, setDescription] = useState(service?.description || '');
  const [price, setPrice] = useState(service?.price?.toString() || '');
  const [durationMinutes, setDurationMinutes] = useState(service?.durationMinutes?.toString() || '30');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await onSubmit({
        name,
        description,
        price: parseFloat(price),
        durationMinutes: parseInt(durationMinutes)
      });
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Erro ao salvar serviço.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white p-6 rounded-xl shadow-lg space-y-4 max-w-md w-full">
      <h2 className="text-xl font-bold text-gray-800">{service ? 'Editar Serviço' : 'Novo Serviço'}</h2>
      
      {error && <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">{error}</div>}

      <Input 
        label="Nome do Serviço" 
        value={name} 
        onChange={(e) => setName(e.target.value)} 
        required 
        placeholder="Ex: Corte de Cabelo"
      />
      
      <div className="flex flex-col gap-1 w-full">
        <label className="text-sm font-medium text-gray-700">Descrição</label>
        <textarea
          className="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 h-20"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Descreva brevemente o que está incluído..."
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <Input 
          label="Preço (R$)" 
          type="number"
          step="0.01"
          value={price} 
          onChange={(e) => setPrice(e.target.value)} 
          required 
        />
        <Input 
          label="Duração (min)" 
          type="number"
          value={durationMinutes} 
          onChange={(e) => setDurationMinutes(e.target.value)} 
          required 
        />
      </div>

      <div className="flex gap-2 pt-4 border-t">
        <Button type="submit" isLoading={loading} className="flex-1">
          {service ? 'Salvar Alterações' : 'Criar Serviço'}
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </form>
  );
};
