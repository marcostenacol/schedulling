import React, { InputHTMLAttributes } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({ label, error, ...props }) => {
  return (
    <div className="flex flex-col gap-1 w-full">
      {label && <label className="text-sm font-medium text-app-muted">{label}</label>}
      <input
        className={`px-3 py-2 border rounded-md shadow-sm bg-app-surface-2 text-app-ink focus:outline-none focus:ring-2 focus:ring-app-accent ${
          error ? 'border-red-500' : 'border-app-border'
        }`}
        {...props}
      />
      {error && <span className="text-xs text-red-500">{error}</span>}
    </div>
  );
};
