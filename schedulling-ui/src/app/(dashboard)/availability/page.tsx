'use client';

import React, { useEffect, useState } from 'react';
import { availabilityApi } from '@/modules/availability/api/availability.api';
import { AvailabilityResponseDTO } from '@/modules/availability/dtos/availability.dto';
import { AvailabilityGrid } from '@/modules/availability/components/AvailabilityGrid';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

export default function AvailabilityPage() {
  const [availabilities, setAvailabilities] = useState<AvailabilityResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [selectedDay, setSelectedDay] = useState(0);
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
    fetchAvailability();
  }, []);

  const handleAddSlot = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await availabilityApi.set({
        dayOfWeek: selectedDay,
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

  if (loading) return <div className="flex justify-center py-20 text-blue-600 font-medium">Carregando agenda...</div>;

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">Minha Agenda</h1>
        <p className="text-gray-500 mt-1">Defina seus horários de atendimento recorrentes para cada dia da semana.</p>
      </div>

      <AvailabilityGrid 
        availabilities={availabilities} 
        onAddSlot={(day) => { setSelectedDay(day); setShowModal(true); }} 
      />

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
          <form onSubmit={handleAddSlot} className="bg-white p-6 rounded-xl shadow-lg space-y-4 max-w-sm w-full">
            <h2 className="text-xl font-bold text-gray-800">Novo Horário</h2>
            <p className="text-sm text-gray-500">Configurando para o dia: <span className="font-bold text-blue-600">{['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'][selectedDay]}</span></p>
            
            <div className="grid grid-cols-2 gap-4">
              <Input label="Início" type="time" value={startTime} onChange={e => setStartTime(e.target.value)} required />
              <Input label="Término" type="time" value={endTime} onChange={e => setEndTime(e.target.value)} required />
            </div>

            <div className="flex gap-2 pt-4 border-t">
              <Button type="submit" isLoading={submitting} className="flex-1">Salvar</Button>
              <Button type="button" variant="secondary" onClick={() => setShowModal(false)} disabled={submitting}>Cancelar</Button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
