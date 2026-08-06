'use client';

import React from 'react';
import { X } from 'lucide-react';
import { AvailabilityResponseDTO } from '../dtos/availability.dto';
import { useLocale } from '@/i18n/LocaleContext';

interface AvailabilityGridProps {
  availabilities: AvailabilityResponseDTO[];
  onAddSlot: (day: number) => void;
  onAddSpecificSlot: () => void;
  onDeleteSlot: (id: string) => void;
}

export const AvailabilityGrid: React.FC<AvailabilityGridProps> = ({ availabilities, onAddSlot, onAddSpecificSlot, onDeleteSlot }) => {
  const { t } = useLocale();
  const DAYS = t.availability.days;
  const recurring = availabilities.filter(a => !a.specificDate);
  const specific = availabilities
    .filter(a => a.specificDate)
    .sort((a, b) => (a.specificDate! < b.specificDate! ? -1 : 1));

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 gap-4">
        {DAYS.map((dayName, index) => {
          const slots = recurring.filter(a => a.dayOfWeek === index);

          return (
            <div key={index} className="bg-app-surface border border-app-border rounded-xl p-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div className="flex flex-col">
                <span className="font-bold text-app-ink">{dayName}</span>
                <span className="text-xs text-app-muted">{t.availability.slotsConfigured(slots.length)}</span>
              </div>

              <div className="flex flex-wrap gap-2 flex-1 md:justify-center">
                {slots.length > 0 ? (
                  slots.map(slot => (
                    <div key={slot.id} className="flex items-center gap-1 bg-app-accent-soft text-app-accent pl-3 pr-1 py-1 rounded-full text-sm font-medium border border-app-accent/20">
                      {slot.startTime.substring(0, 5)} - {slot.endTime.substring(0, 5)}
                      <button
                        onClick={() => onDeleteSlot(slot.id)}
                        className="p-1 rounded-full hover:bg-app-accent/20 transition-colors"
                        title={t.availability.removeSlot}
                      >
                        <X className="w-3 h-3" />
                      </button>
                    </div>
                  ))
                ) : (
                  <span className="text-sm text-app-muted italic">{t.availability.closed}</span>
                )}
              </div>

              <button
                onClick={() => onAddSlot(index)}
                className="text-sm font-semibold text-app-accent hover:bg-app-accent-soft px-3 py-1 rounded-md transition-colors"
              >
                {t.availability.addSlot}
              </button>
            </div>
          );
        })}
      </div>

      <div className="bg-app-surface border border-app-border rounded-xl p-4">
        <div className="flex items-center justify-between mb-3">
          <div>
            <span className="font-bold text-app-ink">{t.availability.specificTitle}</span>
            <p className="text-xs text-app-muted">{t.availability.specificSubtitle}</p>
          </div>
          <button
            onClick={onAddSpecificSlot}
            className="text-sm font-semibold text-app-accent hover:bg-app-accent-soft px-3 py-1 rounded-md transition-colors shrink-0"
          >
            {t.availability.addSlot}
          </button>
        </div>

        <div className="flex flex-wrap gap-2">
          {specific.length > 0 ? (
            specific.map(slot => (
              <div key={slot.id} className="flex items-center gap-1 bg-app-success-soft text-app-success pl-3 pr-1 py-1 rounded-full text-sm font-medium border border-app-success/20">
                {new Date(`${slot.specificDate}T00:00:00`).toLocaleDateString('pt-BR')} · {slot.startTime.substring(0, 5)} - {slot.endTime.substring(0, 5)}
                <button
                  onClick={() => onDeleteSlot(slot.id)}
                  className="p-1 rounded-full hover:bg-app-success/20 transition-colors"
                  title={t.availability.removeSlot}
                >
                  <X className="w-3 h-3" />
                </button>
              </div>
            ))
          ) : (
            <span className="text-sm text-app-muted italic">{t.availability.noSpecificSlots}</span>
          )}
        </div>
      </div>
    </div>
  );
};
