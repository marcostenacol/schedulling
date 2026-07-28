'use client';

import React, { useEffect, useState } from 'react';
import { scheduleApi } from '@/modules/schedule/api/schedule.api';
import { ScheduleResponseDTO } from '@/modules/schedule/dtos/schedule.dto';
import { ScheduleCalendar } from '@/modules/schedule/components/ScheduleCalendar';

export default function SchedulePage() {
  const [schedules, setSchedules] = useState<ScheduleResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedSchedule, setSelectedSchedule] = useState<ScheduleResponseDTO | null>(null);

  const fetchSchedules = async () => {
    try {
      const response = await scheduleApi.listMe();
      setSchedules(response.data.content);
    } catch (err) {
      console.error('Erro ao buscar agendamentos', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSchedules();
  }, []);

  if (loading) return <div className="flex justify-center py-20 text-app-accent font-medium">Carregando agenda...</div>;

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-extrabold text-app-ink tracking-tight">Minha Agenda</h1>
        <p className="text-app-muted mt-1">Visualize e gerencie seus próximos compromissos.</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        <div className="lg:col-span-3">
          <ScheduleCalendar 
            schedules={schedules} 
            onSelectEvent={(event) => setSelectedSchedule(event.resource)} 
          />
        </div>

        <div className="space-y-6">
          <div className="bg-app-surface p-6 rounded-2xl border border-app-border shadow-app-card">
            <h3 className="font-bold text-app-ink mb-4">Detalhes do Agendamento</h3>
            {selectedSchedule ? (
              <div className="space-y-4">
                <div>
                  <label className="text-xs text-app-muted uppercase font-bold tracking-wider">Serviço</label>
                  <p className="text-app-ink font-semibold">{selectedSchedule.serviceName}</p>
                </div>
                <div>
                  <label className="text-xs text-app-muted uppercase font-bold tracking-wider">Cliente</label>
                  <p className="text-app-ink">{selectedSchedule.clientName || 'N/A'}</p>
                </div>
                <div>
                  <label className="text-xs text-app-muted uppercase font-bold tracking-wider">Horário</label>
                  <p className="text-app-ink">{new Date(selectedSchedule.startDateTime).toLocaleString('pt-BR')}</p>
                </div>
                <div className="pt-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                    selectedSchedule.status === 'CONFIRMED' ? 'bg-app-success-soft text-app-success' :
                    selectedSchedule.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' : 'bg-app-surface-2 text-app-muted'
                  }`}>
                    {selectedSchedule.status}
                  </span>
                </div>
              </div>
            ) : (
              <p className="text-app-muted text-sm italic">Selecione um evento no calendário para ver os detalhes.</p>
            )}
          </div>

          <div className="bg-app-accent p-6 rounded-2xl text-app-accent-ink shadow-app-card">
            <h3 className="font-bold mb-2">Resumo da Semana</h3>
            <p className="text-app-accent-ink text-sm">Você tem {schedules.length} agendamentos esta semana.</p>
            <button className="mt-4 w-full bg-app-surface text-app-accent py-2 rounded-xl font-bold text-sm hover:opacity-90 transition-colors">
              Ver relatórios
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
