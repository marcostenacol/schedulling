# Arquitetura — `schedulling-api`

Spring Boot 3.2.5, Java 21, pacote raiz `com.scheduling`.

## Fluxo real

```
Request → Controller → Service (execute()) → Repository → Entity
                              ↕
                            DTO
```

## `base/`

```
base/
├── controller/   # BaseController — helpers de sucesso (ApiResponse)
├── service/      # BaseService<I,O> — interface funcional, método execute()
└── traits/
```

- `BaseService<I, O>`: interface funcional genérica, um único método `execute(I input): O`. Todo Service de módulo implementa essa interface — não há Service com múltiplos métodos de negócio.
- `BaseController`: expõe apenas helpers **de sucesso** (construção de `ApiResponse<T>`) — não há helper de erro no Controller, porque o tratamento de erro é centralizado no `GlobalExceptionHandler` (`shared/exception/`), não no Controller.
- `ApiResponse<T>`: envelope de resposta padrão, formato `{ success, message, data }` — mesmo espírito do envelope usado no `enrollment-api` (projeto vizinho), embora sejam stacks e códigos totalmente independentes.

## `shared/`

```
shared/
├── cache/
├── exception/    # GlobalExceptionHandler (@RestControllerAdvice), AppException
└── security/     # AuthFilter, JwtService, SecurityConfig
```

- **Exceções de negócio** devem estender/lançar `AppException` (ou subtipo) para serem tratadas pelo `GlobalExceptionHandler`. Ele também trata `MethodArgumentNotValidException` (erros de `@Valid`) e tem um branch catch-all para `Exception` genérica.
- **Atenção — débito técnico confirmado**: o branch catch-all do `GlobalExceptionHandler` retorna a mensagem crua da exceção ao cliente. Não reproduza esse padrão ao adicionar um novo tipo de exceção tratada — capture o tipo específico e devolva uma mensagem controlada, em vez de deixar cair no catch-all.
- **Segurança**: JWT stateless via `jjwt` — sem Spring Authorization Server, sem OAuth2. `AuthFilter` intercepta a requisição, `JwtService` gera/valida o token, `SecurityConfig` define as regras de CORS/rota pública vs autenticada. `User implements UserDetails` diretamente, com um único papel por usuário (não há relação N:N usuário↔papéis).
- **Atenção — débito técnico confirmado**: `SecurityConfig` fixa CORS para `http://localhost:3000`, mas a UI real (via `docker-compose.yml`) é exposta em `3001:3000`. Confirme a porta real antes de assumir que o CORS atual cobre o ambiente de homolog/produção via Docker.

## `modules/{domínio}/`

```
modules/
├── auth/
├── schedule/
├── service/
├── profile/
├── availability/
└── admin/
    └── {domínio}/
        ├── controller/
        ├── service/
        ├── repository/
        ├── model/
        └── dto/
```

Cada módulo replica a mesma subestrutura. Módulo de referência real e mais completo: `modules/schedule/`.

### Controller

- `@RestController`, delega ao Service injetado via construtor, retorna `ApiResponse<T>` usando os helpers de `BaseController`.
- Não faz `try/catch` manual para os casos já cobertos pelo `GlobalExceptionHandler` — deixa a exceção propagar.
- Validação de entrada via `@Valid` no DTO do parâmetro do método.

### Service

- Implementa `BaseService<InputDTO, OutputDTO>`, método único `execute()`.
- Um Service por ação (`CreateScheduleService`, `ListSchedulesService`) — nunca um `ScheduleService` genérico com `create()`/`list()`/`update()` juntos.
- Lógica de negócio mora aqui — Repository não tem lógica, Controller não tem lógica.

### Repository

- `JpaRepository<Entity, UUID>`.
- Queries customizadas via `@Query` (JPQL) quando o CRUD padrão não resolve — exemplo real: `ScheduleRepository` tem uma query JPQL de detecção de overlap de horário (não é resolvido via `findBy...` derivado).

### Model/Entity

- `@Entity`, PK `UUID` (não `Long` auto-increment — padrão observado em `Schedule`).
- Lombok (`@Builder`, getters/setters) para reduzir boilerplate.
- Relacionamentos `@ManyToOne` explícitos e tipados (ex.: `Schedule` → `client`, `provider`, `service`).
- Enums de domínio como `enum` Java dedicado, não `String` solta — exemplo real: `ScheduleStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }`.

### DTO

- Um DTO de entrada por ação (`CreateScheduleDTO`) e um de saída por ação/entidade (`ScheduleResponseDTO`) — não reutilize o mesmo DTO para request e response de ações diferentes.
- Sem MapStruct instalado — mapeamento Entity↔DTO é manual (construtor/builder), não gerado.

## Banco de dados — atenção

`spring.jpa.hibernate.ddl-auto: update` no `application.yml` — **não há Flyway nem Liquibase**. O schema é inferido/atualizado automaticamente a partir das entidades JPA em cada boot. Isso é aceitável em ambiente local, mas é risco real em homolog/produção (sem histórico de migração versionado, sem rollback). Não introduza uma migration tool nova sem pedido explícito — mas não assuma que `ddl-auto: update` é um padrão a ser reproduzido/elogiado; é débito técnico documentado, ver `CLAUDE.md`.
