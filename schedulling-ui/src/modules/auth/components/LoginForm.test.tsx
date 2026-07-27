import React from 'react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LoginForm } from './LoginForm';
import { authApi } from '../api/auth.api';
import { useAuthStore } from '../store/auth.store';

const pushMock = vi.fn();

vi.mock('next/navigation', () => ({
  useRouter: () => ({ push: pushMock, replace: vi.fn() }),
}));

vi.mock('../api/auth.api', () => ({
  authApi: {
    login: vi.fn(),
  },
}));

describe('LoginForm', () => {
  beforeEach(() => {
    pushMock.mockClear();
    vi.mocked(authApi.login).mockReset();
    useAuthStore.setState({ token: null });
  });

  const getEmailInput = (container: HTMLElement) =>
    container.querySelector('input[type="email"]') as HTMLInputElement;
  const getPasswordInput = (container: HTMLElement) =>
    container.querySelector('input[type="password"]') as HTMLInputElement;

  it('renders email/password fields and submit button', () => {
    const { container } = render(<LoginForm />);

    expect(getEmailInput(container)).toBeInTheDocument();
    expect(getPasswordInput(container)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Entrar' })).toBeInTheDocument();
  });

  it('stores the token and navigates to /schedule on successful login', async () => {
    vi.mocked(authApi.login).mockResolvedValue({
      success: true,
      message: 'ok',
      data: { accessToken: 'fake-token' } as any,
    });

    const { container } = render(<LoginForm />);

    await userEvent.type(getEmailInput(container), 'user@example.com');
    await userEvent.type(getPasswordInput(container), 'secret123');
    await userEvent.click(screen.getByRole('button', { name: 'Entrar' }));

    await waitFor(() => {
      expect(pushMock).toHaveBeenCalledWith('/schedule');
    });
    expect(useAuthStore.getState().token).toBe('fake-token');
  });

  it('shows an error message when login fails', async () => {
    vi.mocked(authApi.login).mockRejectedValue({
      response: { data: { message: 'Credenciais inválidas' } },
    });

    const { container } = render(<LoginForm />);

    await userEvent.type(getEmailInput(container), 'user@example.com');
    await userEvent.type(getPasswordInput(container), 'wrongpass');
    await userEvent.click(screen.getByRole('button', { name: 'Entrar' }));

    expect(await screen.findByText('Credenciais inválidas')).toBeInTheDocument();
    expect(pushMock).not.toHaveBeenCalled();
  });
});
