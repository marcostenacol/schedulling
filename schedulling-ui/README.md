# schedulling-ui

Frontend da plataforma de agendamento de serviços. Next.js 14 (App Router) consumindo a API em `schedulling-api`.

## Stack

- Next.js 14.2.35, React 18, TypeScript
- Tailwind CSS
- Zustand (com `persist`) para estado global
- axios (instância centralizada em `src/base/api/client.ts`)
- react-big-calendar para a visualização de agenda
- Vitest + Testing Library

## Estrutura

```
src/
├── app/
│   ├── (auth)/         # login/registro
│   └── (dashboard)/     # área autenticada
├── modules/
│   ├── auth/ availability/ profile/ schedule/ service/ admin/
│   │   ├── api/         # chamadas HTTP do módulo
│   │   ├── dtos/        # tipos de request/response
│   │   └── components/
└── base/api/client.ts    # axios central
```

Cada módulo em `src/modules/` espelha um módulo do backend.

## Rodando

```bash
npm install
npm run dev      # dev server, http://localhost:3000
npm run build     # build de produção
npm run lint
npm run test       # Vitest
```

Via Docker (a partir da raiz do monorepo): `docker compose --profile homolog up` — UI exposta em `http://localhost:3001`.

## Variáveis de ambiente

`NEXT_PUBLIC_API_URL` aponta para a API (ver `.env` na raiz do monorepo).
