# Checklist — Novo endpoint (`schedulling-api`)

1. Identificar o módulo em `modules/{domínio}/` (criar a subestrutura `controller/service/repository/model/dto` se o módulo ainda não existir).
2. DTO de entrada em `dto/` com `@Valid`/Bean Validation nos campos obrigatórios.
3. Service novo implementando `BaseService<InputDTO, OutputDTO>`, um Service por ação (`execute()`).
4. Se a ação faz mais de uma escrita relacionada, confirmar que está dentro de um método `@Transactional`.
5. Exceção de negócio via `AppException` (ou subtipo) — não `try/catch` manual no Controller.
6. Controller: `@RestController`, injeta o Service via construtor, retorna `ApiResponse<T>` (helpers de `BaseController`).
7. Repository: método derivado do Spring Data quando possível; `@Query` JPQL só quando o CRUD padrão não resolve (ex.: `ScheduleRepository`, overlap de horário).
8. Confirmar que o retorno de erro para o cliente não expõe detalhe interno cru — se o `GlobalExceptionHandler` ainda não trata o tipo de exceção novo especificamente, ele cai no catch-all que vaza mensagem crua (débito conhecido, ver `rules/architecture-api.md`) — considerar adicionar um branch específico em vez de deixar cair ali.
9. Teste unitário do Service (JUnit 5 + Mockito, AAA, cobrindo caminho feliz e infeliz) — não há teste de Controller/integração no projeto hoje, então não presuma que existe cobertura de borda HTTP.
10. Atualizar anotação OpenAPI/springdoc se o endpoint for relevante para o front consumir.
11. Rodar `./mvnw test` antes de considerar concluído.
