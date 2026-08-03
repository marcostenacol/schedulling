# schedulling-api

Backend da plataforma de agendamento de serviços. Spring Boot 3.2.5 / Java 21.

## Stack

- Spring Boot 3.2.5, Java 21
- PostgreSQL (Flyway para versionamento de schema)
- Redis (cache)
- JWT stateless próprio (`jjwt`), sem Spring Authorization Server/OAuth
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito (unitários) e testes de integração `@WebMvcTest`

## Arquitetura

```
Request → Controller → Service (execute()) → Repository → Entity
```

- Um Service por ação (`CreateScheduleService`, `ListSchedulesService`), implementando `BaseService<I, O>`.
- Respostas sempre no envelope `ApiResponse<T>`.
- Exceções de negócio centralizadas em `GlobalExceptionHandler`.
- Schema versionado via Flyway (`src/main/resources/db/migration`).

Rotas prefixadas com `/api/v1/...`.

## Rodando

Dentro deste diretório, sem Docker:

```bash
./mvnw spring-boot:run
./mvnw test
./mvnw clean package -DskipTests
```

Via Docker (a partir da raiz do monorepo):

```bash
docker compose --profile homolog up
```

API disponível em `http://localhost:8080`, Swagger em `/swagger-ui.html`, health check em `/actuator/health`.

## Variáveis de ambiente

Ver `.env` na raiz do monorepo (`schedulling/.env`): credenciais de banco, `JWT_SECRET`, `SPRING_PROFILES_ACTIVE` (`dev`/`prod`).
