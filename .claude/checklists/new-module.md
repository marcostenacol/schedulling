# Checklist — Novo módulo de domínio (backend + frontend)

## Backend (`schedulling-api`)

1. Criar `modules/{domínio}/` com `controller/service/repository/model/dto` (mesma subestrutura dos módulos existentes: `schedule`, `service`, `profile`, `availability`, `admin`, `auth`).
2. Entity com PK `UUID`, Lombok, relacionamentos `@ManyToOne`/`@OneToMany` explícitos.
3. Enum de domínio dedicado para qualquer status/tipo fechado (seguir `ScheduleStatus` como referência) — nunca `String` solta.
4. Repository estendendo `JpaRepository<Entity, UUID>`.
5. Um Service por ação, implementando `BaseService<I,O>`.
6. Controller `@RestController`, respostas via `ApiResponse<T>`.
7. Exceções de negócio via `AppException`.
8. Teste unitário de cada Service novo.

## Frontend (`schedulling-ui`)

9. Criar `src/modules/{domínio}/` com `api/`, `dtos/`, `components/` — mesmo nome de domínio do módulo backend correspondente.
10. Tipar request/response em `dtos/` — nunca `any`.
11. Chamadas HTTP em `api/`, usando a instância axios central (`base/api/client.ts`) — nunca `fetch` solto em componente.
12. Se o módulo precisar de estado que sobrevive a reload, usar Zustand com `persist` (seguir `auth.store.ts` como referência); caso contrário, estado local do componente.
13. Página(s) no App Router dentro do route group correto (`(auth)` ou `(dashboard)`).

## Ambos

14. Confirmar que o nome do domínio é consistente entre backend e frontend (mesmo nome de módulo nos dois lados).
15. Rodar `./mvnw test` (backend) e `npm run lint` (frontend) antes de considerar concluído — lembrando que o frontend não tem framework de teste automatizado instalado hoje.
