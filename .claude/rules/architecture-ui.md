# Arquitetura — `schedulling-ui`

Next.js 14.2.35 (App Router), React 18, TypeScript 5.

## Estrutura real

```
src/
├── app/
│   ├── (auth)/         # route group — telas de login/registro
│   └── (dashboard)/     # route group — área autenticada
├── modules/
│   ├── admin/
│   ├── auth/
│   ├── availability/
│   ├── profile/
│   ├── schedule/
│   └── service/
│       ├── api/         # chamadas HTTP do módulo (axios)
│       ├── dtos/        # tipos/interfaces de request/response
│       └── components/  # componentes React específicos do módulo
└── base/
    └── api/
        └── client.ts    # instância axios central, guarda token em localStorage
```

Os módulos em `src/modules/` espelham 1:1 os módulos do backend (`modules/{domínio}/` em `schedulling-api`). Módulo novo no front deve corresponder a um módulo real (ou planejado) no back, mantendo o mesmo nome de domínio.

## Padrões observados

- **HTTP**: todas as chamadas de API passam por `api/` dentro do módulo, usando a instância axios central em `base/api/client.ts`. Não chamar `fetch`/`axios` direto de dentro de um componente — sempre via função de módulo em `api/`.
- **Token de autenticação**: guardado em `localStorage` pelo `base/api/client.ts` (interceptor que injeta o header `Authorization`).
  - **Atenção — débito técnico confirmado**: `middleware.ts` na raiz checa a existência de um **cookie** `access_token` para decidir se a rota autenticada pode ser acessada. Como o token real fica em `localStorage`, não em cookie, esse middleware provavelmente nunca vê o token em uso real — o guard de rota do middleware está desalinhado com o mecanismo de auth de fato usado. Não assuma que o middleware protege as rotas hoje; confirme antes de construir algo em cima dele.
- **Estado global**: Zustand. `auth.store.ts` usa o middleware `persist` (sobrevive a reload) e expõe um getter derivado `isAuthenticated` a partir do estado de token/usuário — não um campo booleano solto e potencialmente dessincronizado.
- **Estado local**: `useState`/`useReducer` de componente para o que não precisa sobreviver a navegação/reload.
- **Datas**: há tanto `date-fns` quanto `moment` instalados — **débito técnico**, escolha um ao tocar código de data em vez de introduzir um terceiro; não é pedido para migrar o uso existente do outro, só não adicione uma terceira lib.
- **Calendário**: `react-big-calendar` é a lib usada para a visualização de agenda/horários — qualquer tela nova de calendário deveria reaproveitar essa lib, não introduzir alternativa.
- **Estilização**: Tailwind utilitário direto nas classes JSX — sem CSS Modules nem styled-components no que foi observado.

## Testes

**Não há framework de teste instalado** (`package.json` sem Jest/Vitest/Playwright/Testing Library, sem script `test`). Ver `.claude/rules/testing.md` — qualquer teste novo no frontend exige decidir e instalar a ferramenta primeiro (fora do escopo de uma tarefa de feature isolada, a menos que pedido).
