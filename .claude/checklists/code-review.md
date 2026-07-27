# Checklist — Code review

## Backend (`schedulling-api`)

- [ ] Controller não acessa Repository/Entity direto, nem contém lógica de negócio.
- [ ] Service implementa `BaseService<I,O>` com `execute()`, uma ação por classe.
- [ ] Escrita múltipla relacionada está dentro de um método `@Transactional`.
- [ ] Exceção de negócio é `AppException`/subtipo, não `RuntimeException` genérica não tratada.
- [ ] Nenhum literal solto (`String`/int) representando status/tipo de domínio fechado — deveria ser `enum`.
- [ ] DTO de entrada tem `@Valid` e validação Bean Validation nos campos relevantes.
- [ ] Resposta de sucesso via `ApiResponse<T>` (`BaseController`), não `ResponseEntity` cru montado ad-hoc.
- [ ] PK nova é `UUID`, consistente com o restante do domínio.
- [ ] Existe teste unitário do Service novo/alterado (JUnit 5 + Mockito, feliz + infeliz).
- [ ] Import no topo do arquivo, nunca FQCN inline.

## Frontend (`schedulling-ui`)

- [ ] Chamada HTTP passa por `modules/{domínio}/api/` usando o cliente axios central — não `fetch`/axios solto em componente.
- [ ] Request/response tipados via `dtos/` do módulo — sem `any`.
- [ ] Estado que precisa sobreviver a reload usa Zustand `persist`; o resto é estado local.
- [ ] Nenhuma nova biblioteca de data introduzida além de `date-fns`/`moment` já existentes.
- [ ] Componente/página no route group correto (`(auth)` vs `(dashboard)`).
- [ ] `npm run lint` sem erros novos.

## Geral

- [ ] Mudança não reorganiza pasta/pacote existente sem pedido explícito (Preservação de Código).
- [ ] Identificador técnico em inglês; mensagem ao usuário em português.
- [ ] Se a mudança tocar `docker-compose.yml`/infraestrutura compartilhada, revisar se não introduz/agrava a dependência implícita de `central-db`/`shared-network` documentada em `rules/project-tech.md` — sinalizar, não corrigir silenciosamente sem pedido.
