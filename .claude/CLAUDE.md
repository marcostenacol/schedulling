# Schedulling — Plataforma de Agendamento de Serviços

Você é um Engenheiro de Software Sênior especialista em Spring Boot/Java e Next.js/TypeScript, com foco em boas práticas, SOLID e Clean Code.

Monorepo com dois projetos independentes que juntos formam uma plataforma de agendamento de serviços (estilo Calendly): **`schedulling-api`** (backend Spring Boot) e **`schedulling-ui`** (frontend Next.js). Não há documentação de negócio no repositório (README é o boilerplate padrão do `create-next-app`, sem conteúdo) — o domínio abaixo foi **inferido do código**, não confirmado por nenhum PM/spec.

## Mandato Principal

Antes de sugerir qualquer código, valide se está em conformidade com os padrões em `.claude/rules/`. Se uma solicitação violar os padrões, alerte o usuário e proponha a implementação correta. Este harness documenta **padrões reais observados no código**, não um ideal aspiracional — onde o código é inconsistente ou fraco, isso é sinalizado explicitamente como débito técnico, não normalizado como "padrão a seguir".

## Domínio (inferido do código, não confirmado)

Plataforma de agendamento: **prestadores** (`ROLE_PROVIDER`) cadastram serviços com preço/duração e definem disponibilidade semanal/bloqueios; **clientes** (`ROLE_CLIENT`) reservam horários (`Schedule`, com status `PENDING → CONFIRMED/CANCELLED/COMPLETED`); um papel de **admin** lista usuários. Toda a UI e mensagens estão em português. Sem relação de domínio com o `enrollment-api` (GIZ) — projeto vizinho no mesmo diretório pai, stack e negócio totalmente diferentes.

## Stack

### `schedulling-api`

| Componente | Tecnologia |
|------------|-----------|
| Framework | Spring Boot 3.2.5 |
| Linguagem | Java 21 |
| Banco | PostgreSQL (driver `postgresql`), `ddl-auto: update` (**sem Flyway/Liquibase** — ver débito técnico) |
| Cache/sessão | Redis (`spring-boot-starter-data-redis`) |
| Auth | JWT stateless próprio (`jjwt` 0.12.5) — sem Spring Authorization Server/OAuth |
| Validação | `spring-boot-starter-validation` (Bean Validation) |
| Boilerplate | Lombok |
| Documentação de API | springdoc-openapi 2.5.0 |
| Testes | JUnit 5 + Mockito (unitários apenas) |

### `schedulling-ui`

| Componente | Tecnologia |
|------------|-----------|
| Framework | Next.js 14.2.35, App Router |
| UI | React 18, TypeScript 5, Tailwind 3.4.1 |
| Estado | Zustand 5 (com `persist`) |
| HTTP | axios 1.16 |
| Calendário | react-big-calendar 1.19.4 |
| Datas | `date-fns` **e** `moment` (as duas presentes — ver débito técnico) |
| Testes | nenhum framework instalado, sem script `test` |

## Comandos principais

```bash
make setup       # setup inicial (delega a sub-Makefiles)
make start-api    # sobe a API (ver débito técnico: schedulling-api/ não tem Makefile próprio)
make start-ui     # sobe a UI
make stop         # encerra os containers
make logs         # logs
```

Backend (dentro de `schedulling-api/`, sem Makefile — use Maven direto):
```bash
./mvnw spring-boot:run       # roda a API localmente
./mvnw test                   # roda os testes JUnit
./mvnw clean package -DskipTests   # build (mesmo comando usado no Dockerfile)
```

Frontend (dentro de `schedulling-ui/`):
```bash
npm run dev      # dev server
npm run build     # build de produção
npm run lint      # ESLint
```

Docker (raiz do monorepo, `docker-compose.yml`):
```bash
docker compose --profile homolog up   # sobe api + ui + redis + postgres local (db-scheduling)
docker compose --profile main up      # sobe api + ui + redis, SEM banco local (espera `central-db` externo — ver débito técnico)
```

## Arquitetura — `schedulling-api`

### Fluxo real

```
Request → Controller → Service (execute()) → Repository → Entity
                              ↕
                            DTO
```

- **Controller** (`@RestController`): delega ao Service, retorna `ApiResponse<T>` via helpers de `BaseController`.
- **Service**: implementa `BaseService<I, O>` (interface funcional com `execute()`), uma classe por ação (ex.: `CreateScheduleService`, `ListSchedulesService`) — não um Service por entidade com múltiplos métodos.
- **Repository**: `JpaRepository`, queries JPQL para casos que fogem do CRUD simples (ex.: detecção de overlap de horário em `ScheduleRepository`).
- **Entity**: `@Entity` com Lombok (`@Builder`, `@Getter`/`@Setter` ou `@Data`), PK `UUID`, `@ManyToOne` para relacionamentos.
- **Exceções**: centralizadas em `GlobalExceptionHandler` (`@RestControllerAdvice`) — Controllers/Services não fazem `try/catch` manual para os casos já cobertos ali.

### Organização de pacotes (`com.scheduling`)

```
base/
  controller/   # BaseController — helpers de sucesso (ApiResponse)
  service/      # BaseService<I,O> — interface funcional, execute()
  traits/
shared/
  cache/
  exception/    # GlobalExceptionHandler, AppException
  security/     # AuthFilter, JwtService, SecurityConfig
modules/
  auth/
  schedule/
  service/
  profile/
  availability/
  admin/
    {módulo}/
      controller/
      service/
      repository/
      model/
      dto/
```

Todo módulo novo segue essa mesma subestrutura (`controller/service/repository/model/dto`) dentro de `modules/{domínio}/`. Ver `.claude/rules/architecture-api.md`.

## Arquitetura — `schedulling-ui`

App Router com route groups `(auth)`/`(dashboard)`. `src/modules/{admin,auth,availability,profile,schedule,service}` espelham os módulos do backend, cada um com `api/`, `dtos/`, `components/`. Ver `.claude/rules/architecture-ui.md`.

## Regras de Ouro

1. **Backend**: Controller nunca acessa Repository/Entity direto — sempre via Service. Service sempre implementa `execute()` (padrão `BaseService<I,O>`).
2. **Backend**: uma classe de Service por ação (`CreateScheduleService`, não `ScheduleService` com métodos `create`/`list`/`update`).
3. **Backend**: resposta HTTP sempre via `ApiResponse<T>`/helpers de `BaseController` — nunca `ResponseEntity` cru montado no Controller.
4. **Backend**: exceções de negócio lançam `AppException` (ou subtipo) e são tratadas centralmente em `GlobalExceptionHandler` — não capturar e formatar erro manualmente em cada Controller.
5. **Backend**: entidades com PK `UUID`, Lombok para boilerplate, `@ManyToOne`/`@OneToMany` tipados explicitamente.
6. **Frontend**: chamadas HTTP centralizadas em `api/` de cada módulo (axios), nunca `fetch` solto num componente.
7. **Frontend**: estado global via Zustand com `persist` quando precisa sobreviver a reload (ver `auth.store.ts`); estado local de componente via `useState`/`useReducer`.
8. **Frontend**: estrutura de módulo (`api/`, `dtos/`, `components/`) espelha o domínio do backend — módulo novo no front corresponde a um módulo no back.
9. **Preservação de Código**: não altere código existente sem solicitação explícita.
10. **Idioma do Código**: identificadores em inglês (é o padrão observado no Java; no TS/TSX há mistura — ver `rules/naming.md`). Mensagens ao usuário e nomes de domínio (`Schedule`, `status`) em português quando é isso que já está no código.
11. **Boy Scout Rule**: ao tocar um arquivo, adeque-o aos padrões da camada — sem reestruturar pacote/pasta por conta própria.

## Débito Técnico Conhecido (documentar, não corrigir sem pedido)

- **`schedulling-api` não tem `Makefile` próprio** — os targets `make start-api`/`make setup` no `Makefile` raiz da monorepo tentam delegar a um sub-Makefile que não existe no diretório `schedulling-api/`. Esses comandos provavelmente falham hoje.
- **Sem Flyway/Liquibase**: schema gerenciado por `spring.jpa.hibernate.ddl-auto: update`. Funciona em dev, mas é uma prática arriscada para produção (alterações de schema não versionadas, sem histórico de migração, sem rollback confiável).
- **`GlobalExceptionHandler` vaza mensagem de exceção crua ao cliente** no branch catch-all (`Exception`) — risco de exposição de detalhe interno (stack trace/mensagem de driver JDBC etc.) em resposta HTTP de produção.
- **Inconsistência CORS x porta real da UI**: `SecurityConfig` do backend fixa CORS para `http://localhost:3000`, mas o `docker-compose.yml` expõe a UI em `3001:3000` — ou seja, a UI de fato acessível roda em `3001`, e a config de CORS aponta para uma porta que não é a exposta pelo compose. Isso pode já estar causando bloqueio de CORS em ambiente de homolog/produção via Docker (funciona só quando a UI roda direto em `localhost:3000` fora do compose).
- **`middleware.ts` do frontend checa cookie `access_token`**, mas o cliente HTTP real (`base/api/client.ts`) guarda o token em `localStorage`, não em cookie — o middleware provavelmente nunca vê o token e o guard de rota é inefetivo.
- **`date-fns` e `moment` instalados simultaneamente** no frontend — redundância de dependência para manipulação de data, aumenta bundle sem necessidade clara de ambas.
- **Sem testes de integração/controller** no backend (só unitários com Mockito) e **nenhum framework de teste instalado no frontend** (sem Jest/Vitest/Playwright, sem script `test` no `package.json`).
- **`docker-compose.yml` raiz — perfil `main` depende de infraestrutura externa não declarada no arquivo**: o serviço `api` sob o perfil `main` usa `DB_HOST=central-db` por padrão, mas **não existe serviço `central-db` neste `docker-compose.yml`** (só existe `db-scheduling`, e apenas sob o perfil `homolog`). Isso significa que subir com `--profile main` só funciona se `central-db` já existir em outro stack/host acessível pela rede externa `shared-network` — uma dependência implícita entre projetos que não está documentada em lugar nenhum do repositório. Se esse outro stack (ex.: o do `enrollment-api`) for descontinuado/renomeado/reconfigurado, a API deste projeto quebra silenciosamente sem nenhum sinal local do porquê. Rede `shared-network` também é `external: true` — presumida como criada por outro projeto, sem nenhuma referência de onde/como.
- **`.env` da raiz expõe IP externo real hardcoded** (`NEXT_PUBLIC_API_URL=http://150.230.75.122:8080`) — típico de ambiente de homolog versionado no repo; confirmar se isso deveria estar num `.env` não commitado antes de tratar como valor "de referência" ao criar ambientes novos.
- **Working tree observado como sujo** no momento da criação deste harness (mudanças não commitadas em `package.json`, `package-lock.json`, páginas admin/services, componentes da UI) — não presuma que o estado do repo em disco é o último commit.
- **Sem CI/CD**: não há `.github/workflows/`, `.gitlab-ci.yml`, `Jenkinsfile` nem qualquer outro pipeline configurado — nem para `schedulling-api` (Maven/testes), nem para `schedulling-ui` (lint/build). Nenhum PR/push é validado automaticamente hoje. Configurar isso é uma decisão de infraestrutura a alinhar com o usuário, não um ajuste incidental.

## Auditoria de Segurança/Qualidade — 2026-07-27

Auditoria de segurança/qualidade realizada em 2026-07-27. Itens de segurança foram corrigidos imediatamente; o restante fica registrado como débito técnico conhecido (ver seção acima) até um passo dedicado.

- **[fixed] Segredo JWT com fallback hardcoded em `application.yml`**: `jwt.secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}` tinha um valor default commitado no repositório — se `JWT_SECRET` não fosse injetado no ambiente, a aplicação subia silenciosamente assinando tokens com um segredo público. Corrigido para `jwt.secret: ${JWT_SECRET}` (sem default), forçando falha rápida no startup caso a env var não esteja definida. Confirmado que `docker-compose.yml` (serviço `api`, ambos perfis `homolog`/`main`) já repassa `JWT_SECRET: ${JWT_SECRET}` a partir do `.env` raiz, então o comportamento em runtime não muda quando a env var está presente.
- **[fixed] CORS desalinhado com a porta/URL real da UI**: `SecurityConfig.corsConfigurationSource()` fixava `http://localhost:3000`, mas o `docker-compose.yml` expõe a UI em `3001:3000` e o ambiente de homolog é acessado via IP público. Corrigido para lista explícita: `http://localhost:3001`, `http://150.230.75.122:3001`, `http://150.230.75.122` — mantendo allow-list explícita (não `*`), `allowCredentials(true)` preservado.
- **[known, not yet fixed] Sem Actuator/health endpoint** — não há `spring-boot-starter-actuator` nem endpoint de health/readiness configurado.
- **[known, not yet fixed] Zero uso de framework de logging em `src/main`** — nenhum uso de SLF4J/Logback observado nas camadas de negócio (Controller/Service/Repository); ausência de logging estruturado dificulta troubleshooting e auditoria em produção.
- **[known, not yet fixed] Sem `.dockerignore`** em `schedulling-api/` nem `schedulling-ui/` — builds Docker podem incluir arquivos desnecessários/sensíveis no contexto de build.
- **[known, not yet fixed] Sem prefixo de versionamento de API** (`/v1/...` ou equivalente) — rotas expostas diretamente na raiz, sem estratégia de versionamento para evolução futura sem quebra de compatibilidade.
- **[known, not yet fixed] HikariCP em configuração default; `show-sql`/`format-sql` sempre ativos** — sem tuning de pool de conexões (`maximum-pool-size`, timeouts) explícito em `application.yml`; `show-sql`/`format-sql` do Hibernate ativos incondicionalmente (custo de performance e possível vazamento de SQL em logs de produção).
- **[known, not yet fixed] Sem Spring Profiles (dev/prod)** — configuração única em `application.yml`, sem `application-dev.yml`/`application-prod.yml` nem `spring.profiles.active` diferenciando ambientes.
- **[known, not yet fixed] Sem Checkstyle/Spotless para Java** — nenhuma ferramenta de lint/formatação automática configurada no `pom.xml` para o backend.
- **[known, not yet fixed] Comparação de papel de usuário via string mágica em vez de mapeamento por enum** — em vários pontos o papel do usuário (`ROLE_PROVIDER`/`ROLE_CLIENT`/admin) é comparado/tratado como string solta em vez de usar um `enum` Java dedicado (o mesmo padrão já usado em `ScheduleStatus`), aumentando risco de erro de digitação silencioso e dificultando refactor seguro.

## Git

Histórico real (`git log --oneline --all`, 3 commits no total, cobrindo os dois subprojetos num único repo):
```
87b8259 Standardize infrastructure and profiles
73d3b7a [SECURITY] fix(global): correções de segurança e qualidade
7f854d5 [TASK-FINAL] feat(global): finalização do sistema e preparação para homologação
```

Padrão observado: mensagem livre, às vezes com tag maiúscula entre colchetes (`[SECURITY]`, `[TASK-FINAL]`) seguida de `tipo(escopo): descrição` em português. Não há convenção de ID de ticket. Ver `.claude/rules/git-workflow.md`.

## Regras do Projeto

@.claude/rules/architecture-api.md
@.claude/rules/architecture-ui.md
@.claude/rules/naming.md
@.claude/rules/mandatory.md
@.claude/rules/testing.md
@.claude/rules/git-workflow.md
@.claude/rules/project-tech.md
