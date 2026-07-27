# Regras obrigatórias

## Backend (`schedulling-api`)

1. **Nunca** colocar lógica de negócio no Controller — delegar sempre ao Service.
2. **Nunca** acessar Repository/Entity direto do Controller — sempre via Service.
3. **Sempre** um Service por ação, implementando `BaseService<I, O>` com método `execute()` — nunca um Service de entidade com múltiplos métodos de negócio.
4. **Sempre** retornar `ApiResponse<T>` (helpers de `BaseController`) nas respostas de sucesso — nunca montar `ResponseEntity` cru linha a linha no Controller.
5. **Sempre** lançar exceções de negócio como `AppException` (ou subtipo) para caírem no tratamento central do `GlobalExceptionHandler` — não fazer `try/catch` manual no Controller/Service para casos já cobertos ali.
6. **Nunca** deixar uma exceção genérica não tratada propagar mensagem crua ao cliente ao adicionar um handler novo — capture o tipo específico. (O catch-all atual já faz isso — é débito conhecido, não motivo para reproduzir o padrão em código novo.)
7. **Sempre** usar `@Valid` nos DTOs de entrada do Controller para acionar a validação Bean Validation antes da lógica de negócio.
8. **Sempre** declarar relacionamentos JPA (`@ManyToOne`, `@OneToMany` etc.) explicitamente tipados — nunca id solto sem relação mapeada quando a entidade relacionada já existe no domínio.
9. **Sempre** usar `UUID` como tipo de PK em entidade nova, para manter consistência com `Schedule`/demais entidades já existentes.
10. **Nunca** usar `String`/literal solto para representar um conceito de domínio fechado (status, papel de usuário) — usar `enum` Java dedicado, seguindo o padrão de `ScheduleStatus`.
11. **DB::transaction equivalente**: operações que fazem mais de uma escrita relacionada em uma única ação de negócio devem estar dentro do mesmo método `@Transactional` do Service (ou do Repository, se for esse o nível real de escrita) — confirme se o Service já é transacional antes de assumir atomicidade implícita do Spring.

## Frontend (`schedulling-ui`)

12. **Nunca** chamar `fetch`/`axios` direto dentro de um componente — sempre via função exportada de `modules/{domínio}/api/`.
13. **Sempre** tipar request/response com os tipos de `modules/{domínio}/dtos/` — nunca `any` para dado de API.
14. **Sempre** usar a instância axios central (`base/api/client.ts`) para qualquer chamada nova — não instanciar um novo cliente HTTP à parte.
15. **Nunca** introduzir uma terceira biblioteca de manipulação de data — já há `date-fns` e `moment` convivendo (débito técnico observado); escolha uma das duas existentes para código novo.

## Ambos

16. **Preservação de Código**: não altere código existente sem solicitação explícita.
17. **Importações**: sempre no topo do arquivo — nunca import inline/dinâmico sem necessidade real (code-splitting explícito no Next.js é a única exceção aceitável).
18. **Idioma do Código**: identificadores técnicos em inglês; mensagens ao usuário final em português.
