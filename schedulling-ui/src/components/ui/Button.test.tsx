import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Button } from './Button';

describe('Button', () => {
  it('renders its children and reacts to clicks', async () => {
    const onClick = vi.fn();
    render(<Button onClick={onClick}>Salvar</Button>);

    const button = screen.getByRole('button', { name: 'Salvar' });
    await userEvent.click(button);

    expect(onClick).toHaveBeenCalledTimes(1);
  });

  it('is disabled and does not fire onClick while isLoading', async () => {
    const onClick = vi.fn();
    render(
      <Button onClick={onClick} isLoading>
        Salvar
      </Button>
    );

    const button = screen.getByRole('button', { name: 'Salvar' });
    expect(button).toBeDisabled();

    await userEvent.click(button);
    expect(onClick).not.toHaveBeenCalled();
  });
});
