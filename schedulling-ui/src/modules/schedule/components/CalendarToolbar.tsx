'use client';

import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import type { ToolbarProps, View } from 'react-big-calendar';

const VIEW_LABELS: Record<string, string> = {
  month: 'Mês',
  week: 'Semana',
  day: 'Dia',
  agenda: 'Agenda',
};

export function CalendarToolbar<TEvent extends object>(props: ToolbarProps<TEvent>) {
  const { label, view, views, onNavigate, onView } = props;
  const viewOptions = Array.isArray(views) ? views : Object.keys(views);

  return (
    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-4 pb-4 border-b border-app-border">
      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => onNavigate('TODAY')}
          className="px-3 py-1.5 rounded-md text-sm font-semibold border border-app-border text-app-ink hover:bg-app-surface-2 transition-colors"
        >
          Hoje
        </button>
        <div className="flex items-center rounded-md border border-app-border overflow-hidden">
          <button
            type="button"
            onClick={() => onNavigate('PREV')}
            className="p-1.5 text-app-muted hover:bg-app-surface-2 hover:text-app-ink transition-colors"
            aria-label="Anterior"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          <button
            type="button"
            onClick={() => onNavigate('NEXT')}
            className="p-1.5 text-app-muted hover:bg-app-surface-2 hover:text-app-ink transition-colors border-l border-app-border"
            aria-label="Próximo"
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
        <span className="font-bold text-app-ink capitalize">{label}</span>
      </div>

      <div className="flex items-center gap-0.5 p-1 bg-app-surface-2 rounded-lg self-start sm:self-auto">
        {viewOptions.map((v) => (
          <button
            key={v}
            type="button"
            onClick={() => onView(v as View)}
            className={`min-w-[4.5rem] px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
              view === v ? 'bg-app-accent text-app-accent-ink shadow-app-card' : 'text-app-muted hover:text-app-ink'
            }`}
          >
            {VIEW_LABELS[v] ?? v}
          </button>
        ))}
      </div>
    </div>
  );
}
