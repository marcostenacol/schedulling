import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

// Vitest config for schedulling-ui (Next.js 14 App Router).
// Kept separate from next.config.js: Next.js does not ship a first-party
// Jest/Vitest preset for the App Router, so component tests run against a
// plain Vite + jsdom environment with the same "@/*" path alias used by
// tsconfig.json. See .claude/CLAUDE.md (débito técnico: "nenhum framework de
// teste instalado no frontend").
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  test: {
    environment: 'jsdom',
    setupFiles: ['./vitest.setup.ts'],
    globals: true,
    css: false,
  },
});
