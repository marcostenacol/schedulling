'use client';

import React, { useState } from 'react';
import { AxiosError } from 'axios';
import { CreateServiceDTO, ServiceResponseDTO, UpdateServiceDTO } from '../dtos/service.dto';
import { ApiResponse } from '../../auth/dtos/auth.dto';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { useLocale } from '@/i18n/LocaleContext';

interface ServiceFormProps {
  service?: ServiceResponseDTO;
  onSuccess: () => void;
  onCancel: () => void;
  onSubmit: (data: CreateServiceDTO | UpdateServiceDTO) => Promise<ApiResponse<ServiceResponseDTO>>;
}

export const ServiceForm: React.FC<ServiceFormProps> = ({ service, onSuccess, onCancel, onSubmit }) => {
  const { t } = useLocale();
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
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      setError(axiosError.response?.data?.message || t.services.saveError);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-app-surface border border-app-border p-6 rounded-xl shadow-app-card space-y-4 max-w-md w-full max-h-[90vh] overflow-y-auto">
      <h2 className="text-xl font-bold text-app-ink">{service ? t.services.editTitle : t.services.createTitle}</h2>

      {error && <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">{error}</div>}

      <Input
        label={t.services.nameLabel}
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
        placeholder={t.services.namePlaceholder}
      />

      <div className="flex flex-col gap-1 w-full">
        <label className="text-sm font-medium text-app-muted">{t.common.descriptionLabel}</label>
        <textarea
          className="px-3 py-2 border border-app-border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent h-20"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder={t.services.descriptionPlaceholder}
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <Input
          label={t.services.priceLabel}
          type="number"
          step="0.01"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          required
        />
        <Input
          label={t.services.durationLabel}
          type="number"
          value={durationMinutes}
          onChange={(e) => setDurationMinutes(e.target.value)}
          required
        />
      </div>

      <div className="flex gap-2 pt-4 border-t border-app-border">
        <Button type="submit" isLoading={loading} className="flex-1">
          {service ? t.common.saveChanges : t.services.createAction}
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel} disabled={loading}>
          {t.common.cancel}
        </Button>
      </div>
    </form>
  );
};
