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

  if (loading) return <div className="flex justify-center py-20 text-blue-600 font-medium">Carregando agenda...</div>;

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">Minha Agenda</h1>
        <p className="text-gray-500 mt-1">Visualize e gerencie seus próximos compromissos.</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
        <div className="lg:col-span-3">
          <ScheduleCalendar 
            schedules={schedules} 
            onSelectEvent={(event) => setSelectedSchedule(event.resource)} 
          />
        </div>

        <div className="space-y-6">
          <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
            <h3 className="font-bold text-gray-900 mb-4">Detalhes do Agendamento</h3>
            {selectedSchedule ? (
              <div className="space-y-4">
                <div>
                  <label className="text-xs text-gray-400 uppercase font-bold tracking-wider">Serviço</label>
                  <p className="text-gray-900 font-semibold">{selectedSchedule.serviceName}</p>
                </div>
                <div>
                  <label className="text-xs text-gray-400 uppercase font-bold tracking-wider">Cliente</label>
                  <p className="text-gray-900">{selectedSchedule.clientName || 'N/A'}</p>
                </div>
                <div>
                  <label className="text-xs text-gray-400 uppercase font-bold tracking-wider">Horário</label>
                  <p className="text-gray-900">{new Date(selectedSchedule.startDateTime).toLocaleString('pt-BR')}</p>
                </div>
                <div className="pt-4">
                  <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                    selectedSchedule.status === 'CONFIRMED' ? 'bg-green-100 text-green-700' : 
                    selectedSchedule.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' : 'bg-gray-100 text-gray-700'
                  }`}>
                    {selectedSchedule.status}
                  </span>
                </div>
              </div>
            ) : (
              <p className="text-gray-400 text-sm italic">Selecione um evento no calendário para ver os detalhes.</p>
            )}
          </div>

          <div className="bg-blue-600 p-6 rounded-2xl text-white shadow-lg shadow-blue-200">
            <h3 className="font-bold mb-2">Resumo da Semana</h3>
            <p className="text-blue-100 text-sm">Você tem {schedules.length} agendamentos esta semana.</p>
            <button className="mt-4 w-full bg-white text-blue-600 py-2 rounded-xl font-bold text-sm hover:bg-blue-50 transition-colors">
              Ver relatórios
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
