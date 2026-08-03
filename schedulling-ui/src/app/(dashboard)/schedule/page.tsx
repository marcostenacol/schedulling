'use client';

import React, { useEffect, useState } from 'react';
import { Plus } from 'lucide-react';
import { scheduleApi } from '@/modules/schedule/api/schedule.api';
import { ScheduleResponseDTO, ScheduleStatus } from '@/modules/schedule/dtos/schedule.dto';
import { ScheduleCalendar } from '@/modules/schedule/components/ScheduleCalendar';
import { CreateScheduleModal } from '@/modules/schedule/components/CreateScheduleModal';
import { Button } from '@/components/ui/Button';
import { useProfileStore } from '@/modules/profile/store/profile.store';

const STATUS_LABELS: Record<ScheduleStatus, string> = {
  [ScheduleStatus.PENDING]: 'Pendente',
  [ScheduleStatus.CONFIRMED]: 'Confirmado',
  [ScheduleStatus.CANCELLED]: 'Cancelado',
  [ScheduleStatus.COMPLETED]: 'Concluído',
};

export default function SchedulePage() {
  const [schedules, setSchedules] = useState<ScheduleResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedSchedule, setSelectedSchedule] = useState<ScheduleResponseDTO | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const profile = useProfileStore((state) => state.profile);

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

  const handleStatusChange = async (status: ScheduleStatus) => {
    if (!selectedSchedule) return;
    setUpdatingStatus(true);
    try {
      const response = await scheduleApi.updateStatus(selectedSchedule.id, status);
      setSelectedSchedule(response.data);
      fetchSchedules();
    } catch (err) {
      console.error('Erro ao alterar status do agendamento', err);
    } finally {
      setUpdatingStatus(false);
    }
  };

  const isProvider = !!profile && selectedSchedule?.providerId === profile.id;
  const isClient = !!profile && selectedSchedule?.clientId === profile.id;
  const isFinalStatus = selectedSchedule?.status === ScheduleStatus.CANCELLED || selectedSchedule?.status === ScheduleStatus.COMPLETED;

  if (loading) return <div className="flex justify-center py-20 text-app-accent font-medium">Carregando agenda...</div>;

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold text-app-ink tracking-tight">Minha Agenda</h1>
          <p className="text-app-muted mt-1">Visualize e gerencie seus próximos compromissos.</p>
        </div>
        <Button onClick={() => setShowCreateModal(true)} className="flex items-center gap-2 shrink-0">
          <Plus className="h-5 w-5" />
          Novo Agendamento
        </Button>
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
                <div className="pt-2">
                  <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                    selectedSchedule.status === 'CONFIRMED' ? 'bg-app-success-soft text-app-success' :
                    selectedSchedule.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                    selectedSchedule.status === 'CANCELLED' ? 'bg-red-100 text-red-700' : 'bg-app-surface-2 text-app-muted'
                  }`}>
                    {STATUS_LABELS[selectedSchedule.status]}
                  </span>
                </div>

                {!isFinalStatus && (isProvider || isClient) && (
                  <div className="flex flex-col gap-2 pt-4 border-t border-app-border">
                    {isProvider && selectedSchedule.status === ScheduleStatus.PENDING && (
                      <Button onClick={() => handleStatusChange(ScheduleStatus.CONFIRMED)} isLoading={updatingStatus} className="w-full">
                        Confirmar
                      </Button>
                    )}
                    {isProvider && selectedSchedule.status === ScheduleStatus.CONFIRMED && (
                      <Button onClick={() => handleStatusChange(ScheduleStatus.COMPLETED)} isLoading={updatingStatus} className="w-full">
                        Marcar como concluído
                      </Button>
                    )}
                    {(isProvider || isClient) && (
                      <Button
                        variant="danger"
                        onClick={() => handleStatusChange(ScheduleStatus.CANCELLED)}
                        isLoading={updatingStatus}
                        className="w-full"
                      >
                        Cancelar agendamento
                      </Button>
                    )}
                  </div>
                )}
              </div>
            ) : (
              <p className="text-app-muted text-sm italic">Selecione um evento no calendário para ver os detalhes.</p>
            )}
          </div>

          <div className="bg-app-accent p-6 rounded-2xl text-app-accent-ink shadow-app-card">
            <h3 className="font-bold mb-2">Resumo da Semana</h3>
            <p className="text-app-accent-ink text-sm">Você tem {schedules.length} agendamentos esta semana.</p>
          </div>
        </div>
      </div>

      {showCreateModal && (
        <CreateScheduleModal
          onClose={() => setShowCreateModal(false)}
          onSuccess={() => { setShowCreateModal(false); fetchSchedules(); }}
        />
      )}
    </div>
  );
}
