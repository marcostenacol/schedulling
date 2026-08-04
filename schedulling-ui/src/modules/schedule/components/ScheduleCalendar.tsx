'use client';

import React from 'react';
import { Calendar, dateFnsLocalizer } from 'react-big-calendar';
import format from 'date-fns/format';
import parse from 'date-fns/parse';
import startOfWeek from 'date-fns/startOfWeek';
import getDay from 'date-fns/getDay';
import ptBR from 'date-fns/locale/pt-BR';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { ScheduleResponseDTO } from '../dtos/schedule.dto';
import { CalendarToolbar } from './CalendarToolbar';

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
        components={{ toolbar: CalendarToolbar }}
        eventPropGetter={() => ({
          className: 'bg-app-accent text-app-accent-ink rounded-md border-none text-xs px-2 py-1'
        })}
      />
    </div>
  );
};
