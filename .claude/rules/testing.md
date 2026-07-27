# Testes

## Backend (`schedulling-api`) — JUnit 5 + Mockito

**Estado real**: existem 10 arquivos de teste reais em `src/test/java/com/scheduling/modules/...`, todos **unitários** (Service com dependências mockadas via Mockito) — não há teste de integração/Controller (sem `@SpringBootTest`/`MockMvc` observado).

Padrão real observado:
- Estilo AAA (arrange → act → assert).
- `@DisplayName` em português, descrevendo o comportamento testado.
- Cobre caminho feliz **e** caminho infeliz (input inválido, entidade não encontrada, etc.) — não só o happy path.
- Mock nas bordas (Repository injetado no Service) — não mocka o próprio objeto sob teste.

```java
@Test
@DisplayName("Deve lançar exceção ao criar agendamento com horário sobreposto")
void deveLancarExcecaoQuandoHorarioSobreposto() {
    // arrange
    when(scheduleRepository.existsOverlap(any())).thenReturn(true);

    // act + assert
    assertThrows(AppException.class, () -> createScheduleService.execute(dto));
}
```

Rodar:
```bash
./mvnw test                          # tudo
./mvnw test -Dtest=CreateScheduleServiceTest   # um arquivo
```

**Gap real a considerar antes de expandir cobertura**: como não há teste de Controller/integração, um bug na camada HTTP (mapeamento de rota, serialização do `ApiResponse`, `@Valid` não disparando) não é pego por nenhum teste hoje. Se for pedido para aumentar cobertura, teste de integração (`@SpringBootTest` + `MockMvc` ou `WebTestClient`) é o gap mais valioso a preencher primeiro — mas só monte essa infraestrutura se for pedido explicitamente, não de passagem numa tarefa de feature.

## Frontend (`schedulling-ui`)

**Estado real**: nenhum framework de teste instalado (`package.json` sem Jest/Vitest/Testing Library/Playwright, sem script `test`). Antes de escrever o primeiro teste de frontend, é necessário decidir e instalar a ferramenta — isso é uma decisão de arquitetura à parte, não algo a fazer de passagem dentro de uma tarefa de feature. Não presuma qual framework "deveria" ser usado sem confirmar com o time.

## O que testar quando houver ferramenta — regra de bolso

Teste primeiro o que, se quebrar, dói: cálculo de disponibilidade/overlap de horário, transições de status de agendamento (`PENDING/CONFIRMED/CANCELLED/COMPLETED`), autenticação/JWT. Getter/setter trivial e código sem lógica não precisam de teste dedicado.
