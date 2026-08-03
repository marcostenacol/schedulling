# Schedulling

Plataforma de agendamento de serviços (estilo Calendly): prestadores cadastram serviços e disponibilidade, clientes reservam horários. Projeto de portfólio, feito para praticar um backend Spring Boot completo (JWT próprio, Flyway, testes de integração) ao lado de um frontend Next.js consumindo essa API.

## Stack

| | |
|---|---|
| API | Spring Boot 3.2.5, Java 21, PostgreSQL, Redis, JWT (jjwt) |
| UI | Next.js 14 (App Router), React 18, TypeScript, Tailwind, Zustand |

Mais detalhes de arquitetura e convenções em `schedulling-api/README.md` e `schedulling-ui/README.md`.

## Rodando localmente

```bash
docker compose --profile homolog up
```

Sobe API + UI + Redis + Postgres local, sem depender de nenhuma infraestrutura externa.

| Serviço | URL |
|---|---|
| UI | http://localhost:3001 |
| API | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |

O perfil `main` (`docker compose --profile main up`) espera um Postgres externo (`central-db`) compartilhado com outros projetos do monorepo — não sobe um banco próprio.

## Comandos úteis

```bash
make setup       # setup inicial
make start-api    # sobe só a API
make start-ui     # sobe só a UI
make stop         # encerra os containers
make logs         # logs
```
