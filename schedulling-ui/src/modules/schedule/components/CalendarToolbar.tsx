'use client';

import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import type { ToolbarProps, View } from 'react-big-calendar';
import { useLocale } from '@/i18n/LocaleContext';

export function CalendarToolbar<TEvent extends object>(props: ToolbarProps<TEvent>) {
  const { t } = useLocale();
  const { label, view, views, onNavigate, onView } = props;
  const viewLabels: Record<string, string> = {
    month: t.calendar.month,
    week: t.calendar.week,
    day: t.calendar.day,
    agenda: t.calendar.agenda,
  };
  const viewOptions = Array.isArray(views) ? views : Object.keys(views);

  return (
    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4 pb-4 border-b border-app-border">
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => onNavigate('TODAY')}
          className="px-3 py-1.5 rounded-full text-sm font-semibold border border-app-border text-app-ink hover:bg-app-surface-2 transition-colors"
        >
          {t.calendar.today}
        </button>
        <div className="flex items-center rounded-full border border-app-border overflow-hidden">
          <button
            type="button"
            onClick={() => onNavigate('PREV')}
            className="p-1.5 text-app-muted hover:bg-app-surface-2 hover:text-app-ink transition-colors"
            aria-label={t.calendar.previous}
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          <button
            type="button"
            onClick={() => onNavigate('NEXT')}
            className="p-1.5 text-app-muted hover:bg-app-surface-2 hover:text-app-ink transition-colors border-l border-app-border"
            aria-label={t.calendar.next}
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
        <span className="font-bold text-app-ink capitalize">{label}</span>
      </div>

      <div className="flex items-center gap-0.5 p-1 bg-app-surface-2 rounded-full self-start sm:self-auto">
        {viewOptions.map((v) => (
          <button
            key={v}
            type="button"
            onClick={() => onView(v as View)}
            className={`min-w-[4.5rem] px-3 py-1.5 rounded-full text-sm font-medium transition-colors ${
              view === v ? 'bg-app-accent text-app-accent-ink shadow-app-card' : 'text-app-muted hover:text-app-ink'
            }`}
          >
            {viewLabels[v] ?? v}
          </button>
        ))}
      </div>
    </div>
  );
}
