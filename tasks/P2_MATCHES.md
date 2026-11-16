# PERSON 2 — Matches, score, standings

**Начинай после foundation PR P1-09.** В корне обязательно открой `AGENTS.md`, `docs/03_CONTRACTS.md`, `docs/04_TEAM_TASKS.md`, `docs/06_DESIGN_SYSTEM.md`.

**Твои файлы:** `ui/matches/**`, `ui/standings/**`, `domain/StandingsCalculator.kt`, соответствующие unit tests. **Не трогай** `AppViewModel`, navigation, models, Gradle или theme.

## Чек-лист (точные критерии см. `docs/04_TEAM_TASKS.md`)

- [ ] P2-01: MatchRow для planned/finished матча
- [ ] P2-02: MatchesScreen список + Back
- [ ] P2-03: empty state и минимум 2 команды
- [ ] P2-04: Create Match Dialog
- [ ] P2-05: обработка ошибок выбора команд
- [ ] P2-06: ScoreScreen с двумя полями
- [ ] P2-07: сохранение и ошибки счёта
- [ ] P2-08: StandingsCalculator 3/1/0, tie-breakers
- [ ] P2-09: StandingsScreen table
- [ ] P2-10: unit tests calculator и ручная проверка обновления счёта

## Важные правила

- В калькулятор передаются только команды/матчи **одного турнира**.
- Нельзя считать матч с `null`-счётом.
- После изменения 3:1 → 0:0 таблица считается заново, без дублей.
- По `docs/03_CONTRACTS.md` должно компилироваться **без изменения AppNavGraph** (P1 подключит позже).
- Прямой доступ к repository из composable запрещён; только callback.

**PR ревью:** P1. **Защита:** ScoreScreen, callbacks, валидаторы, очки, Goal Difference, unit tests.

**Codex:** возьми P2 prompt из `prompts/ROLE_PROMPTS.md`, укажи конкретный ID. Только своя feature branch, PR без merge.
