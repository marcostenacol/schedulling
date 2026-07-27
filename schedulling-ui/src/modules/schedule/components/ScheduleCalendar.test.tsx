import React from 'react';
import { describe, it, expect, vi, beforeAll } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ScheduleCalendar } from './ScheduleCalendar';
import { ScheduleResponseDTO, ScheduleStatus } from '../dtos/schedule.dto';

// react-big-calendar reads layout APIs jsdom doesn't implement; stub the
// minimal set it needs so the component can mount without a real browser.
beforeAll(() => {
  window.matchMedia =
    window.matchMedia ||
    ((query: string) =>
      ({
        matches: false,
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn(),
      }) as unknown as MediaQueryList);
});

const schedule: ScheduleResponseDTO = {
  id: '1',
  clientId: 'c1',
  clientName: 'Maria',
  providerId: 'p1',
  providerName: 'João',
  serviceName: 'Corte de cabelo',
  // Uses "today" so the event falls inside the calendar's default month view.
  startDateTime: new Date(new Date().setHours(10, 0, 0, 0)).toISOString(),
  endDateTime: new Date(new Date().setHours(10, 30, 0, 0)).toISOString(),
  status: ScheduleStatus.PENDING,
  price: 50,
};

describe('ScheduleCalendar', () => {
  it('renders the calendar with an event built from the given schedule', () => {
    render(<ScheduleCalendar schedules={[schedule]} onSelectEvent={vi.fn()} />);

    expect(screen.getByText('Corte de cabelo - Maria')).toBeInTheDocument();
  });

  it('renders an empty calendar without crashing when there are no schedules', () => {
    render(<ScheduleCalendar schedules={[]} onSelectEvent={vi.fn()} />);

    expect(screen.queryByText('Corte de cabelo - Maria')).not.toBeInTheDocument();
  });
});
