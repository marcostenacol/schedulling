'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Plus, PackageSearch } from 'lucide-react';
import { serviceApi } from '@/modules/service/api/service.api';
import { ServiceResponseDTO, CreateServiceDTO, UpdateServiceDTO } from '@/modules/service/dtos/service.dto';
import { ServiceCard } from '@/modules/service/components/ServiceCard';
import { ServiceForm } from '@/modules/service/components/ServiceForm';
import { Button } from '@/components/ui/Button';
import { useProfileStore } from '@/modules/profile/store/profile.store';
import { useLocale } from '@/i18n/LocaleContext';

export default function ServicesPage() {
  const { t } = useLocale();
  const router = useRouter();
  const profile = useProfileStore((state) => state.profile);
  const isProvider = profile?.type === 'provider' || profile?.type === 'admin';
  const [services, setServices] = useState<ServiceResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingService, setEditingService] = useState<ServiceResponseDTO | undefined>(undefined);

  useEffect(() => {
    if (profile && !isProvider) {
      router.replace('/schedule');
    }
  }, [profile, isProvider, router]);

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
    if (isProvider) fetchServices();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isProvider]);

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

  const handleDelete = async (service: ServiceResponseDTO) => {
    if (!window.confirm(t.services.deleteConfirm(service.name))) return;
    try {
      await serviceApi.delete(service.id);
      fetchServices();
    } catch (err) {
      console.error('Erro ao excluir serviço', err);
    }
  };

  if (profile && !isProvider) return null;
  if (loading) return <div className="flex justify-center py-20 text-app-accent font-medium">{t.services.loading}</div>;

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-extrabold text-app-ink tracking-tight">{t.services.pageTitle}</h1>
          <p className="text-app-muted mt-1">{t.services.pageSubtitle}</p>
        </div>
        <Button onClick={() => setShowForm(true)} className="flex items-center gap-2">
          <Plus className="h-5 w-5" />
          {t.services.newService}
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
            <PackageSearch className="h-8 w-8 text-app-muted" />
          </div>
          <h3 className="text-lg font-semibold text-app-ink">{t.services.emptyTitle}</h3>
          <p className="text-app-muted mb-6">{t.services.emptySubtitle}</p>
          <Button variant="secondary" onClick={() => setShowForm(true)}>{t.services.emptyAction}</Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {services.map(service => (
            <ServiceCard
              key={service.id}
              service={service}
              onEdit={() => { setEditingService(service); setShowForm(true); }}
              onDelete={() => handleDelete(service)}
            />
          ))}
        </div>
      )}
    </div>
  );
}
