'use client';

import React, { useEffect, useState } from 'react';
import { serviceApi } from '@/modules/service/api/service.api';
import { ServiceResponseDTO, CreateServiceDTO, UpdateServiceDTO } from '@/modules/service/dtos/service.dto';
import { ServiceCard } from '@/modules/service/components/ServiceCard';
import { ServiceForm } from '@/modules/service/components/ServiceForm';
import { Button } from '@/components/ui/Button';

export default function ServicesPage() {
  const [services, setServices] = useState<ServiceResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingService, setEditingService] = useState<ServiceResponseDTO | undefined>(undefined);

  const fetchServices = async () => {
    try {
      const response = await serviceApi.listMe();
      setServices(response.data.content);
    } catch (err) {
      console.error('Erro ao buscar serviços', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServices();
  }, []);

  const handleCreate = async (data: CreateServiceDTO | UpdateServiceDTO) => {
    return serviceApi.create(data as CreateServiceDTO);
  };

  const handleUpdate = async (data: CreateServiceDTO | UpdateServiceDTO) => {
    if (!editingService) throw new Error('Nenhum serviço selecionado para edição.');
    return serviceApi.update(editingService.id, data as UpdateServiceDTO);
  };

  const handleSuccess = () => {
    setShowForm(false);
    setEditingService(undefined);
    fetchServices();
  };

  if (loading) return <div className="flex justify-center py-20 text-app-accent font-medium">Carregando serviços...</div>;

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-extrabold text-app-ink tracking-tight">Meus Serviços</h1>
          <p className="text-app-muted mt-1">Gerencie os tipos de serviços que você oferece aos seus clientes.</p>
        </div>
        <Button onClick={() => setShowForm(true)} className="flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Novo Serviço
        </Button>
      </div>

      {showForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
          <ServiceForm 
            service={editingService}
            onCancel={() => { setShowForm(false); setEditingService(undefined); }}
            onSuccess={handleSuccess}
            onSubmit={editingService ? handleUpdate : handleCreate}
          />
        </div>
      )}

      {services.length === 0 ? (
        <div className="bg-app-surface border-2 border-dashed border-app-border rounded-2xl p-12 text-center">
          <div className="bg-app-surface-2 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-app-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          </div>
          <h3 className="text-lg font-semibold text-app-ink">Nenhum serviço cadastrado</h3>
          <p className="text-app-muted mb-6">Comece adicionando seu primeiro serviço para que os clientes possam agendar.</p>
          <Button variant="secondary" onClick={() => setShowForm(true)}>Adicionar Serviço</Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {services.map(service => (
            <ServiceCard 
              key={service.id} 
              service={service} 
              onEdit={() => { setEditingService(service); setShowForm(true); }} 
            />
          ))}
        </div>
      )}
    </div>
  );
}
