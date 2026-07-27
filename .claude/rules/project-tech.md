# Configuração Técnica — Schedulling

## Serviços (docker-compose.yml, raiz do monorepo)

| Serviço | Imagem/build | Porta | Perfil |
|---------|--------------|-------|--------|
| `api` | build `./schedulling-api` | `8080:8080` | `homolog`, `main` |
| `ui` | build `./schedulling-ui` | `3001:3000` | `homolog`, `main` |
| `cache-scheduling` | `redis:7-alpine` | interno | `homolog`, `main` |
| `db-scheduling` | `postgres:16-alpine` | `5435:5432` (configurável) | **somente `homolog`** |

Rede: `shared-network`, declarada como `external: true` — presumidamente criada por outro stack do monorepo (ex.: `enrollment-api`), não por este `docker-compose.yml`. Não há documentação de onde/como essa rede é criada.

**Atenção — dependência implícita não documentada**: sob o perfil `main`, o serviço `api` usa `DB_HOST=central-db` como default, mas **não existe serviço `central-db` neste arquivo** — só `db-scheduling`, disponível apenas sob `homolog`. Rodar `docker compose --profile main up` pressupõe que `central-db` já existe externamente (outro host/stack acessível via `shared-network`). Isso é uma dependência cross-projeto implícita e um ponto real de fragilidade operacional — sinalizar antes de "corrigir" subindo um banco local sob o perfil `main`, pois pode não ser essa a intenção original.

## Variáveis de ambiente (raiz `.env`)

```
DB_HOST / DB_PORT / DB_NAME / DB_USER / DB_PASSWORD           # produção (central-db externo)
DB_HOST_HOMOLOG / DB_PORT_HOMOLOG / ... (defaults no compose)  # homolog (db-scheduling local)
REDIS_HOST=cache-scheduling / REDIS_PORT=6379
JWT_SECRET                                                     # compartilhado entre ambientes no .env observado
NEXT_PUBLIC_API_URL=http://150.230.75.122:8080                 # IP externo real hardcoded — confirmar antes de tratar como valor de referência
```

**Atenção**: o `.env` da raiz observado tem um IP público real como valor de `NEXT_PUBLIC_API_URL`. Se este arquivo estiver versionado no repositório (confirme antes de assumir que é um `.env.example`), isso é exposição de infraestrutura de produção em texto plano no controle de versão.

## `schedulling-api`

- Spring Boot 3.2.5, Java 21, `groupId=com.scheduling`, `artifactId=schedulling-api`.
- Dependências principais: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-data-redis`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `lombok`, driver `postgresql`, `springdoc-openapi-starter-webmvc-ui` 2.5.0, `jjwt-api`/`jjwt-impl`/`jjwt-jackson` 0.12.5.
- Sem MapStruct — mapeamento Entity↔DTO manual.
- `application.yml`: `spring.jpa.hibernate.ddl-auto: update`, porta 8080.
- Dockerfile: multi-stage, `maven:3.9.6-eclipse-temurin-21-alpine` (build, `-DskipTests`) → `eclipse-temurin:21-jre-alpine` (runtime), expõe 8080.
- **Sem `Makefile` próprio** neste diretório — comandos usam Maven Wrapper direto (`./mvnw ...`).

## `schedulling-ui`

- Next.js 14.2.35 (App Router), React 18, TypeScript 5.
- Tailwind 3.4.1, Zustand 5.0.13 (com `persist`), axios 1.16, `react-big-calendar` 1.19.4.
- `date-fns` e `moment` ambos instalados (redundância, ver `CLAUDE.md` — débito técnico).
- Sem framework de teste instalado.
- Dockerfile: 3 estágios, `node:20-alpine`, build `standalone` do Next.js, usuário non-root, expõe 3000 (mapeado para `3001` externamente pelo compose).
- `middleware.ts`: guarda de rota baseado em cookie `access_token` — **desalinhado** com o mecanismo real de auth (token vive em `localStorage`, injetado via interceptor axios em `base/api/client.ts`), ver `rules/architecture-ui.md`.

## Comandos Docker

```bash
docker compose --profile homolog up     # api + ui + redis + postgres local
docker compose --profile main up        # api + ui + redis, espera central-db externo (ver aviso acima)
```
