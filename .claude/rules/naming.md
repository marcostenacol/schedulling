# Convenções de nomenclatura

## `schedulling-api` (Java)

| Camada | Padrão | Exemplos reais |
|--------|--------|-----------------|
| Entity/Model | `PascalCase`, nome singular | `Schedule`, `User` |
| Repository | `{Entity}Repository` | `ScheduleRepository` |
| Service | `{Ação}{Entidade}Service`, implementa `BaseService<I,O>` | `CreateScheduleService`, `ListSchedulesService` |
| Controller | `{Entidade}Controller` | `ScheduleController` |
| DTO de entrada | `{Ação}{Entidade}DTO` | `CreateScheduleDTO` |
| DTO de saída | `{Entidade}ResponseDTO` | `ScheduleResponseDTO` |
| Enum | `{Conceito}` (sem sufixo `Enum` observado no Java, diferente de outros projetos do monorepo) | `ScheduleStatus` |
| Exceção de negócio | estende/deriva de `AppException` | — |

Identificadores em inglês. Método principal do Service: sempre `execute()` (contrato de `BaseService<I,O>`).

## `schedulling-ui` (TypeScript)

- Componentes React: `PascalCase.tsx`.
- Store Zustand: `{domínio}.store.ts` (ex.: `auth.store.ts`).
- Cliente HTTP: `client.ts` (instância axios central em `base/api/`).
- DTOs/tipos: em `modules/{domínio}/dtos/`, nome do tipo em `PascalCase`.
- **Observação real**: há mistura de idioma em identificadores no TS/TSX (nomes de domínio e alguns campos em português refletindo o negócio, ex.: `Schedule`, `status` em inglês, mas rótulos/mensagens sempre em português) — diferente do Java, que é mais consistentemente em inglês. Ao criar código novo, prefira inglês para identificador técnico (variável, função, tipo) e português só para texto voltado ao usuário final (labels, mensagens de erro/sucesso) — mas não renomeie identificadores existentes em português só por causa desta regra (Preservação de Código).

## Boy Scout Rule

Ao tocar um arquivo, adeque nomenclatura/tipagem/imports aos padrões da camada — sem mover arquivo de pasta nem trocar padrão arquitetural por conta própria.
