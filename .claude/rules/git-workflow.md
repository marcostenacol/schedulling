# Git workflow

## Estado real observado

O diretório `/home/ubuntu/projetos/schedulling` é um **único repositório git** contendo ambos `schedulling-api/` e `schedulling-ui/` (não são dois repos separados). Histórico total (`git log --oneline --all`) tem apenas 3 commits:

```
87b8259 Standardize infrastructure and profiles
73d3b7a [SECURITY] fix(global): correções de segurança e qualidade
7f854d5 [TASK-FINAL] feat(global): finalização do sistema e preparação para homologação
```

## Convenção de commit observada

Mensagem livre, às vezes precedida de uma tag maiúscula entre colchetes indicando a natureza da mudança (`[SECURITY]`, `[TASK-FINAL]`), seguida do padrão `tipo(escopo): descrição` em português (`fix(global): ...`, `feat(global): ...`). **Não há convenção de ID de ticket/Jira** observada neste projeto (diferente do `enrollment-api` vizinho, que usa `[GIZ-XXX]`) — não invente um prefixo de ticket para este repo sem confirmar com o time.

Ao criar um commit novo, siga o padrão real: `tipo(escopo): descrição` em português, com tag maiúscula entre colchetes apenas quando fizer sentido destacar a natureza da mudança (ex.: `[SECURITY]`) — não é obrigatório para todo commit.

## Estado do working tree

No momento da criação deste harness, o working tree estava **sujo** (mudanças não commitadas em `package.json`, `package-lock.json`, páginas de admin/serviços e componentes da UI). Não presuma que o código em disco corresponde ao último commit — confira `git status`/`git diff` antes de assumir uma baseline.

## Antes de qualquer mudança

Sempre `git fetch origin` (se houver remoto configurado) e `git status` antes de criar branch ou editar arquivo, para não basear trabalho em estado desatualizado ou perder mudanças locais não commitadas de terceiros.

## Recomendação (sem confirmação de fluxo real de branch)

Não há evidência no histórico de um fluxo de branch estabelecido (feature → homolog → main, ou similar) — os 3 commits observados foram aparentemente direto na branch principal. Não assuma um fluxo de PR/branch específico deste projeto sem confirmar com o time antes de abrir uma branch nova.
