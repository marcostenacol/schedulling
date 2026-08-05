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
  [ScheduleStatus.PENDING]: '#B87A16',
  [ScheduleStatus.CONFIRMED]: '#B0263B',
  [ScheduleStatus.CANCELLED]: '#9C8F7E',
  [ScheduleStatus.COMPLETED]: '#4E6B3A',
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

interface SlotSelection {
  start: Date;
  end: Date;
}

interface ScheduleCalendarProps {
  schedules: ScheduleResponseDTO[];
  onSelectEvent: (event: CalendarEvent) => void;
  onSelectSlot?: (slot: SlotSelection) => void;
}

export const ScheduleCalendar: React.FC<ScheduleCalendarProps> = ({ schedules, onSelectEvent, onSelectSlot }) => {
  const events: CalendarEvent[] = schedules.map(s => ({
    id: s.id,
    title: `${s.serviceName} - ${s.clientName || 'Cliente'}`,
    start: new Date(s.startDateTime),
    end: new Date(s.endDateTime),
    resource: s
  }));

  return (
    <div
      className={`nb-dogear h-[600px] bg-app-surface p-4 rounded-2xl shadow-app-card border border-app-border ${
        onSelectSlot ? 'nb-selectable' : ''
      }`}
    >
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
        selectable={Boolean(onSelectSlot)}
        onSelectSlot={onSelectSlot}
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
                  className="nb-stamp ml-auto shrink-0"
                  style={{ backgroundColor: `${STATUS_COLOR[event.resource.status]}1f`, color: STATUS_COLOR[event.resource.status] }}
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
