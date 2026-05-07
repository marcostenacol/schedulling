'use client';

import React from 'react';
import { AvailabilityResponseDTO } from '../dtos/availability.dto';

interface AvailabilityGridProps {
  availabilities: AvailabilityResponseDTO[];
  onAddSlot: (day: number) => void;
}

const DAYS = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];

export const AvailabilityGrid: React.FC<AvailabilityGridProps> = ({ availabilities, onAddSlot }) => {
  return (
    <div className="grid grid-cols-1 gap-4">
      {DAYS.map((dayName, index) => {
        const slots = availabilities.filter(a => a.dayOfWeek === index);
        
        return (
          <div key={index} className="bg-white border border-gray-200 rounded-xl p-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="flex flex-col">
              <span className="font-bold text-gray-800">{dayName}</span>
              <span className="text-xs text-gray-400">{slots.length} horários configurados</span>
            </div>
            
            <div className="flex flex-wrap gap-2 flex-1 md:justify-center">
              {slots.length > 0 ? (
                slots.map(slot => (
                  <div key={slot.id} className="bg-blue-50 text-blue-700 px-3 py-1 rounded-full text-sm font-medium border border-blue-100">
                    {slot.startTime.substring(0, 5)} - {slot.endTime.substring(0, 5)}
                  </div>
                ))
              ) : (
                <span className="text-sm text-gray-300 italic">Fechado</span>
              )}
            </div>

            <button 
              onClick={() => onAddSlot(index)}
              className="text-sm font-semibold text-blue-600 hover:bg-blue-50 px-3 py-1 rounded-md transition-colors"
            >
              + Adicionar
            </button>
          </div>
        );
      })}
    </div>
  );
};
