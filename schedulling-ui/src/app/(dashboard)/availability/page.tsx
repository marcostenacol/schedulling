'use client';

import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { availabilityApi } from '@/modules/availability/api/availability.api';
import { AvailabilityResponseDTO } from '@/modules/availability/dtos/availability.dto';
import { AvailabilityGrid } from '@/modules/availability/components/AvailabilityGrid';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { useProfileStore } from '@/modules/profile/store/profile.store';

const DAYS = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];

export default function AvailabilityPage() {
  const router = useRouter();
  const profile = useProfileStore((state) => state.profile);
  const isProvider = profile?.type === 'provider' || profile?.type === 'admin';
  const [availabilities, setAvailabilities] = useState<AvailabilityResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [mode, setMode] = useState<'recurring' | 'specific'>('recurring');
  const [selectedDay, setSelectedDay] = useState(0);
  const [specificDate, setSpecificDate] = useState('');
  const [startTime, setStartTime] = useState('09:00');
  const [endTime, setEndTime] = useState('18:00');
  const [submitting, setSubmitting] = useState(false);

  const fetchAvailability = async () => {
    try {
      const res = await availabilityApi.listMe();
      setAvailabilities(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (profile && !isProvider) {
      router.replace('/schedule');
      return;
    }
    if (isProvider) fetchAvailability();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [profile, isProvider, router]);

  const openRecurringModal = (day: number) => {
    setMode('recurring');
    setSelectedDay(day);
    setShowModal(true);
  };

  const openSpecificModal = () => {
    setMode('specific');
    setSpecificDate('');
    setShowModal(true);
  };

  const handleAddSlot = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await availabilityApi.set({
        ...(mode === 'recurring' ? { dayOfWeek: selectedDay } : { specificDate }),
        startTime: startTime + ':00',
        endTime: endTime + ':00',
        active: true
      });
      setShowModal(false);
      fetchAvailability();
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteSlot = async (id: string) => {
    if (!window.confirm('Remover este horário?')) return;
    try {
      await availabilityApi.delete(id);
      fetchAvailability();
    } catch (err) {
      console.error(err);
    }
  };

  if (profile && !isProvider) return null;
  if (loading) return <div className="flex justify-center py-20 text-app-accent font-medium">Carregando agenda...</div>;

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-extrabold text-app-ink tracking-tight">Minha Agenda</h1>
        <p className="text-app-muted mt-1">Defina seus horários de atendimento recorrentes ou avulsos.</p>
      </div>

      <AvailabilityGrid
        availabilities={availabilities}
        onAddSlot={openRecurringModal}
        onAddSpecificSlot={openSpecificModal}
        onDeleteSlot={handleDeleteSlot}
      />

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
          <form onSubmit={handleAddSlot} className="bg-app-surface border border-app-border p-6 rounded-xl shadow-app-card space-y-4 max-w-sm w-full max-h-[90vh] overflow-y-auto">
            <h2 className="text-xl font-bold text-app-ink">Novo Horário</h2>

            {mode === 'recurring' ? (
              <p className="text-sm text-app-muted">Configurando para o dia: <span className="font-bold text-app-accent">{DAYS[selectedDay]}</span></p>
            ) : (
              <Input label="Data" type="date" value={specificDate} onChange={e => setSpecificDate(e.target.value)} required />
            )}

            <div className="grid grid-cols-2 gap-4">
              <Input label="Início" type="time" value={startTime} onChange={e => setStartTime(e.target.value)} required />
              <Input label="Término" type="time" value={endTime} onChange={e => setEndTime(e.target.value)} required />
            </div>

            <div className="flex gap-2 pt-4 border-t border-app-border">
              <Button type="submit" isLoading={submitting} className="flex-1">Salvar</Button>
              <Button type="button" variant="secondary" onClick={() => setShowModal(false)} disabled={submitting}>Cancelar</Button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
