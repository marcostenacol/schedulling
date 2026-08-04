'use client';

import React from 'react';
import { Calendar, dateFnsLocalizer } from 'react-big-calendar';
import format from 'date-fns/format';
import parse from 'date-fns/parse';
import startOfWeek from 'date-fns/startOfWeek';
import getDay from 'date-fns/getDay';
import ptBR from 'date-fns/locale/pt-BR';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { ScheduleResponseDTO, ScheduleStatus } from '../dtos/schedule.dto';
import { CalendarToolbar } from './CalendarToolbar';

const STATUS_COLOR: Record<ScheduleStatus, string> = {
  [ScheduleStatus.PENDING]: '#d97706',
  [ScheduleStatus.CONFIRMED]: '#0f766e',
  [ScheduleStatus.CANCELLED]: '#94a3b8',
  [ScheduleStatus.COMPLETED]: '#16a34a',
};

const STATUS_LABEL: Record<ScheduleStatus, string> = {
  [ScheduleStatus.PENDING]: 'Pendente',
  [ScheduleStatus.CONFIRMED]: 'Confirmado',
  [ScheduleStatus.CANCELLED]: 'Cancelado',
  [ScheduleStatus.COMPLETED]: 'Concluído',
};

const locales = {
  'pt-BR': ptBR,
};

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

interface CalendarEvent {
  id: string;
  title: string;
  start: Date;
  end: Date;
  resource: ScheduleResponseDTO;
}

interface ScheduleCalendarProps {
  schedules: ScheduleResponseDTO[];
  onSelectEvent: (event: CalendarEvent) => void;
}

export const ScheduleCalendar: React.FC<ScheduleCalendarProps> = ({ schedules, onSelectEvent }) => {
  const events: CalendarEvent[] = schedules.map(s => ({
    id: s.id,
    title: `${s.serviceName} - ${s.clientName || 'Cliente'}`,
    start: new Date(s.startDateTime),
    end: new Date(s.endDateTime),
    resource: s
  }));

  return (
    <div className="h-[600px] bg-app-surface p-4 rounded-2xl shadow-app-card border border-app-border">
      <Calendar
        localizer={localizer}
        events={events}
        startAccessor="start"
        endAccessor="end"
        culture="pt-BR"
        messages={{
          next: "Próximo",
          previous: "Anterior",
          today: "Hoje",
          month: "Mês",
          week: "Semana",
          day: "Dia",
          agenda: "Agenda",
          date: "Data",
          time: "Horário",
          event: "Evento",
          allDay: "Dia inteiro",
          noEventsInRange: "Nenhum agendamento neste período.",
          showMore: (total) => `+ ${total} mais`
        }}
        onSelectEvent={onSelectEvent}
        components={{
          toolbar: CalendarToolbar,
          event: ({ event }) => (
            <div className="flex items-center gap-1.5 overflow-hidden">
              <span
                className="w-1.5 h-1.5 rounded-full shrink-0"
                style={{ backgroundColor: STATUS_COLOR[event.resource.status] }}
              />
              <span className="font-semibold shrink-0">{format(event.start, 'HH:mm')}</span>
              <span className="truncate opacity-90">{event.resource.serviceName}</span>
            </div>
          ),
          agenda: {
            event: ({ event }) => (
              <div className="flex items-center gap-2 py-1 cursor-pointer">
                <span
                  className="w-2.5 h-2.5 rounded-full shrink-0"
                  style={{ backgroundColor: STATUS_COLOR[event.resource.status] }}
                />
                <span className="font-semibold text-app-ink">{event.resource.serviceName}</span>
                <span className="text-app-muted">— {event.resource.clientName || 'Cliente'}</span>
                <span
                  className="ml-auto shrink-0 px-2 py-0.5 rounded-full text-xs font-semibold"
                  style={{ backgroundColor: `${STATUS_COLOR[event.resource.status]}22`, color: STATUS_COLOR[event.resource.status] }}
                >
                  {STATUS_LABEL[event.resource.status]}
                </span>
              </div>
            ),
          },
        }}
        eventPropGetter={() => ({
          className: 'rbc-event-status rounded-md border-none text-xs px-2 py-1 cursor-pointer',
        })}
      />
    </div>
  );
};
