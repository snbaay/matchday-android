# PERSON 4 — Tournaments UI + Theme

**Начинай после foundation PR P1-09.** Сначала прочитай `CONTRIBUTING.md`, `docs/03_CONTRACTS.md`, `docs/04_TEAM_TASKS.md`, `docs/06_DESIGN_SYSTEM.md`.

**Твои файлы:** `ui/tournaments/**`, `ui/theme/**`, соответствующие тесты. **Не трогай** repository, AppViewModel, navigation, Gradle, Teams, Matches.

## Чек-лист (критерии см. `docs/04_TEAM_TASKS.md`)

- [ ] P4-01: Color.kt цвета
- [ ] P4-02: Theme.kt и Type.kt, Material 3
- [ ] P4-03: TournamentCard component
- [ ] P4-04: TournamentListScreen с LazyColumn
- [ ] P4-05: create tournament dialog
- [ ] P4-06: обработка ошибок Result.failure
- [ ] P4-07: TournamentDashboardScreen live counts
- [ ] P4-08: кнопки Teams / Matches / Standings
- [ ] P4-09: пустой список и аккуратные отступы
- [ ] P4-10: previews, long names, small screen QA

## Что важно

Никаких моковых счётчиков после интеграции. `teamCount`/`matchCount` уже передаёт P1: твой экран **только отображает** их. Стиль — `docs/06_DESIGN_SYSTEM.md`. Любые изменения сигнатур согласовывать с P1.

**PR ревью:** P1. **Защита:** Material 3 theme, карточки, dialog, state, callbacks и переходы.

**Порядок работы.** Возьми шаблон P4 из `prompts/ROLE_PROMPTS.md` и выполняй одну задачу за раз. Работай в своей ветке и передай PR на ревью.
