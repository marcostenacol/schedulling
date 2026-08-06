'use client';

import React from 'react';
import { Calendar, dateFnsLocalizer } from 'react-big-calendar';
import format from 'date-fns/format';
import parse from 'date-fns/parse';
import startOfWeek from 'date-fns/startOfWeek';
import getDay from 'date-fns/getDay';
import ptBR from 'date-fns/locale/pt-BR';
import enUS from 'date-fns/locale/en-US';
import esLocale from 'date-fns/locale/es';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import { ScheduleResponseDTO, ScheduleStatus } from '../dtos/schedule.dto';
import { CalendarToolbar } from './CalendarToolbar';
import { useLocale } from '@/i18n/LocaleContext';

const STATUS_COLOR: Record<ScheduleStatus, string> = {
  [ScheduleStatus.PENDING]: '#B87A16',
  [ScheduleStatus.CONFIRMED]: '#B0263B',
  [ScheduleStatus.CANCELLED]: '#9C8F7E',
  [ScheduleStatus.COMPLETED]: '#4E6B3A',
};

const locales = {
  'pt-BR': ptBR,
  'en-US': enUS,
  es: esLocale,
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
  const { t } = useLocale();

  const statusLabels: Record<ScheduleStatus, string> = {
    [ScheduleStatus.PENDING]: t.schedule.statusPending,
    [ScheduleStatus.CONFIRMED]: t.schedule.statusConfirmed,
    [ScheduleStatus.CANCELLED]: t.schedule.statusCancelled,
    [ScheduleStatus.COMPLETED]: t.schedule.statusCompleted,
  };

  const events: CalendarEvent[] = schedules.map(s => ({
    id: s.id,
    title: `${s.serviceName} - ${s.clientName || t.calendar.defaultClient}`,
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
        culture={t.common.dateFnsLocale}
        messages={{
          next: t.calendar.next,
          previous: t.calendar.previous,
          today: t.calendar.today,
          month: t.calendar.month,
          week: t.calendar.week,
          day: t.calendar.day,
          agenda: t.calendar.agenda,
          date: t.calendar.date,
          time: t.calendar.time,
          event: t.calendar.event,
          allDay: t.calendar.allDay,
          noEventsInRange: t.calendar.noEventsInRange,
          showMore: (total) => t.calendar.showMore(total),
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
                <span className="text-app-muted">— {event.resource.clientName || t.calendar.defaultClient}</span>
                <span
                  className="nb-stamp ml-auto shrink-0"
                  style={{ backgroundColor: `${STATUS_COLOR[event.resource.status]}1f`, color: STATUS_COLOR[event.resource.status] }}
                >
                  {statusLabels[event.resource.status]}
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
