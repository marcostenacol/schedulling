'use client';

import React, { useEffect, useState } from 'react';
import { AxiosError } from 'axios';
import { X, AlertTriangle } from 'lucide-react';
import { serviceApi } from '@/modules/service/api/service.api';
import { availabilityApi } from '@/modules/availability/api/availability.api';
import { scheduleApi } from '../api/schedule.api';
import { ServiceResponseDTO } from '@/modules/service/dtos/service.dto';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';

interface CreateScheduleModalProps {
  onClose: () => void;
  onSuccess: () => void;
}

type Mode = 'own' | 'search';

export const CreateScheduleModal: React.FC<CreateScheduleModalProps> = ({ onClose, onSuccess }) => {
  const [mode, setMode] = useState<Mode>('own');
  const [ownServices, setOwnServices] = useState<ServiceResponseDTO[]>([]);
  const [providerCode, setProviderCode] = useState('');
  const [searching, setSearching] = useState(false);
  const [foundServices, setFoundServices] = useState<ServiceResponseDTO[] | null>(null);

  const [serviceId, setServiceId] = useState('');
  const [date, setDate] = useState('');
  const [slots, setSlots] = useState<string[]>([]);
  const [selectedSlot, setSelectedSlot] = useState<string | null>(null);
  const [customTime, setCustomTime] = useState('');
  const [guestName, setGuestName] = useState('');
  const [notes, setNotes] = useState('');
  const [durationMinutes, setDurationMinutes] = useState<number | ''>('');
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [confirmingOutsideHours, setConfirmingOutsideHours] = useState(false);

  const services = mode === 'own' ? ownServices : (foundServices ?? []);
  const selectedService = services.find(s => s.id === serviceId);
  const chosenTime = selectedSlot ?? (customTime ? `${customTime}:00` : null);
  const isOutsideAvailability = !!chosenTime && !slots.includes(chosenTime);

  useEffect(() => {
    serviceApi.listMe()
      .then(res => setOwnServices(res.data.content))
      .catch(() => setOwnServices([]));
  }, []);

  useEffect(() => {
    setServiceId('');
  }, [mode]);

  useEffect(() => {
    setDurationMinutes(selectedService?.durationMinutes ?? '');
  }, [selectedService]);

  useEffect(() => {
    setSelectedSlot(null);
    setCustomTime('');
    setConfirmingOutsideHours(false);
    if (!serviceId || !date || !selectedService) {
      setSlots([]);
      return;
    }

    setLoadingSlots(true);
    availabilityApi
      .getSlots(selectedService.providerId, serviceId, date)
      .then(res => setSlots(res.data))
      .catch(() => setError('Erro ao carregar horários disponíveis.'))
      .finally(() => setLoadingSlots(false));
  }, [serviceId, date, selectedService]);

  const handleSearchProvider = async () => {
    if (!providerCode.trim()) return;
    setSearching(true);
    setError('');
    setFoundServices(null);
    try {
      const res = await serviceApi.listByProvider(providerCode.trim());
      if (res.data.content.length === 0) {
        setError('Nenhum serviço ativo encontrado para este prestador.');
      }
      setFoundServices(res.data.content);
    } catch {
      setError('Prestador não encontrado. Confira o código.');
    } finally {
      setSearching(false);
    }
  };

  const createSchedule = async () => {
    if (!selectedService || !chosenTime) return;

    setSubmitting(true);
    setError('');
    try {
      await scheduleApi.create({
        providerId: selectedService.providerId,
        serviceId: selectedService.id,
        startDateTime: `${date}T${chosenTime}`,
        ...(mode === 'own' && guestName ? { guestName } : {}),
        ...(mode === 'own' && notes ? { notes } : {}),
        ...(mode === 'own' && durationMinutes && durationMinutes !== selectedService.durationMinutes
          ? { durationMinutes: Number(durationMinutes) }
          : {}),
      });
      onSuccess();
    } catch (err) {
      const axiosError = err as AxiosError<{ message?: string }>;
      setError(axiosError.response?.data?.message || 'Erro ao criar agendamento.');
      setConfirmingOutsideHours(false);
    } finally {
      setSubmitting(false);
    }
  };

  const handleConfirmClick = () => {
    if (isOutsideAvailability) {
      setConfirmingOutsideHours(true);
      return;
    }
    createSchedule();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
      <div className="bg-app-surface border border-app-border p-6 rounded-xl shadow-app-card space-y-4 max-w-md w-full max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-bold text-app-ink">Novo Agendamento</h2>
          <button onClick={onClose} className="text-app-muted hover:text-app-ink">
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && <div className="p-3 text-sm text-red-700 bg-red-100 rounded-md">{error}</div>}

        {confirmingOutsideHours ? (
          <div className="space-y-4">
            <div className="p-4 rounded-lg bg-yellow-100 text-yellow-800 flex gap-3">
              <AlertTriangle className="w-5 h-5 shrink-0 mt-0.5" />
              <p className="text-sm">
                <strong>{chosenTime?.substring(0, 5)}</strong> está fora do horário de atendimento cadastrado
                pelo prestador para este dia. Tem certeza que quer agendar mesmo assim?
              </p>
            </div>
            <div className="flex gap-2">
              <Button onClick={createSchedule} isLoading={submitting} className="flex-1">
                Sim, agendar mesmo assim
              </Button>
              <Button type="button" variant="secondary" onClick={() => setConfirmingOutsideHours(false)} disabled={submitting}>
                Voltar
              </Button>
            </div>
          </div>
        ) : (
          <>
            <div className="flex items-center gap-1 p-1 bg-app-surface-2 rounded-lg">
              <button
                type="button"
                onClick={() => setMode('own')}
                className={`flex-1 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                  mode === 'own' ? 'bg-app-accent text-app-accent-ink' : 'text-app-muted hover:text-app-ink'
                }`}
              >
                Meu serviço
              </button>
              <button
                type="button"
                onClick={() => setMode('search')}
                className={`flex-1 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                  mode === 'search' ? 'bg-app-accent text-app-accent-ink' : 'text-app-muted hover:text-app-ink'
                }`}
              >
                Agendar com prestador
              </button>
            </div>

            {mode === 'own' ? (
              <div className="flex flex-col gap-1 w-full">
                <label className="text-sm font-medium text-app-muted">Serviço</label>
                <select
                  className="px-3 py-2 border border-app-border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent"
                  value={serviceId}
                  onChange={e => setServiceId(e.target.value)}
                >
                  <option value="">Selecione um serviço</option>
                  {ownServices.map(s => (
                    <option key={s.id} value={s.id}>
                      {s.name} — R$ {s.price.toFixed(2)}
                    </option>
                  ))}
                </select>
                {ownServices.length === 0 && (
                  <span className="text-xs text-app-muted">Você ainda não cadastrou nenhum serviço.</span>
                )}
              </div>
            ) : (
              <div className="flex flex-col gap-2">
                <div className="flex flex-col gap-1 w-full">
                  <label className="text-sm font-medium text-app-muted">Código do prestador</label>
                  <div className="flex gap-2">
                    <input
                      className="flex-1 px-3 py-2 border border-app-border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent"
                      value={providerCode}
                      onChange={e => setProviderCode(e.target.value)}
                      placeholder="Cole o código/link recebido do prestador"
                    />
                    <Button type="button" onClick={handleSearchProvider} isLoading={searching}>
                      Buscar
                    </Button>
                  </div>
                </div>

                {foundServices && foundServices.length > 0 && (
                  <div className="flex flex-col gap-1 w-full">
                    <label className="text-sm font-medium text-app-muted">Serviço</label>
                    <select
                      className="px-3 py-2 border border-app-border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent"
                      value={serviceId}
                      onChange={e => setServiceId(e.target.value)}
                    >
                      <option value="">Selecione um serviço</option>
                      {foundServices.map(s => (
                        <option key={s.id} value={s.id}>
                          {s.name} ({s.providerName}) — R$ {s.price.toFixed(2)}
                        </option>
                      ))}
                    </select>
                  </div>
                )}
              </div>
            )}

            {mode === 'own' && selectedService && (
              <>
                <div className="p-3 rounded-md bg-app-accent-soft text-app-accent text-xs font-medium">
                  Agendamento walk-in — cliente sem conta no sistema.
                </div>
                <Input
                  label="Nome do cliente (opcional)"
                  value={guestName}
                  onChange={e => setGuestName(e.target.value)}
                  placeholder="Ex: João da Silva"
                />
                <div className="flex flex-col gap-1 w-full">
                  <label className="text-sm font-medium text-app-muted">Descrição / observações (opcional)</label>
                  <textarea
                    className="px-3 py-2 border border-app-border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent h-20"
                    value={notes}
                    onChange={e => setNotes(e.target.value)}
                    placeholder="Alguma observação sobre este atendimento..."
                  />
                </div>
                <Input
                  label={`Duração em minutos (padrão do serviço: ${selectedService.durationMinutes}min)`}
                  type="number"
                  min={1}
                  value={durationMinutes}
                  onChange={e => setDurationMinutes(e.target.value === '' ? '' : Number(e.target.value))}
                />
              </>
            )}

            <Input label="Data" type="date" value={date} onChange={e => setDate(e.target.value)} />

            {serviceId && date && (
              <div className="flex flex-col gap-2">
                <label className="text-sm font-medium text-app-muted">Horários disponíveis</label>
                {loadingSlots ? (
                  <span className="text-sm text-app-muted italic">Carregando...</span>
                ) : slots.length > 0 ? (
                  <div className="flex flex-wrap gap-2">
                    {slots.map(slot => (
                      <button
                        key={slot}
                        type="button"
                        onClick={() => { setSelectedSlot(slot); setCustomTime(''); }}
                        className={`px-3 py-1.5 rounded-md text-sm font-semibold border transition-colors ${
                          selectedSlot === slot
                            ? 'bg-app-accent text-app-accent-ink border-app-accent'
                            : 'bg-app-surface-2 text-app-ink border-app-border hover:border-app-accent'
                        }`}
                      >
                        {slot.substring(0, 5)}
                      </button>
                    ))}
                  </div>
                ) : (
                  <span className="text-sm text-app-muted italic">Nenhum horário disponível nesta data.</span>
                )}

                <div className="pt-2">
                  <Input
                    label="Ou escolha outro horário (fora da disponibilidade cadastrada)"
                    type="time"
                    value={customTime}
                    onChange={e => { setCustomTime(e.target.value); setSelectedSlot(null); }}
                  />
                </div>
              </div>
            )}

            <div className="flex gap-2 pt-4 border-t border-app-border">
              <Button
                type="button"
                onClick={handleConfirmClick}
                isLoading={submitting}
                disabled={!chosenTime}
                className="flex-1"
              >
                Confirmar agendamento
              </Button>
              <Button type="button" variant="secondary" onClick={onClose} disabled={submitting}>
                Cancelar
              </Button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};
