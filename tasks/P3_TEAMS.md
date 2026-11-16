# PERSON 3 — Teams screen + common UI components

**Начинай после foundation PR P1-09.** Сначала прочитай `CONTRIBUTING.md`, `docs/03_CONTRACTS.md`, `docs/04_TEAM_TASKS.md`, `docs/06_DESIGN_SYSTEM.md`.

**Твои файлы:** `ui/teams/**`, `ui/components/**`, тесты в соответствующих пакетах. **Не трогай** ViewModel, модели, навигацию, другие экраны и тему.

## Чек-лист (критерии приёмки см. `docs/04_TEAM_TASKS.md`)

- [ ] P3-01: TeamsScreen header + Back
- [ ] P3-02: LazyColumn списка команд
- [ ] P3-03: reusable TeamRow
- [ ] P3-04: dialog добавления команды
- [ ] P3-05: пустой ввод и trim
- [ ] P3-06: отображение ошибки имени-дубликата
- [ ] P3-07: закрытие dialog после успешного добавления, очистка поля
- [ ] P3-08: reusable EmptyState
- [ ] P3-09: reusable AppTopBar
- [ ] P3-10: previews/accessibility + small screen проверка

## Что считается выполненной задачей

Реальное изменение UI, которое можно открыть и показать. `TeamsScreen` должен принимать только параметры, указанные в `docs/03_CONTRACTS.md`. Логику создания команды реализует **P1**, ты только вызываешь `onAddTeam(name)` и показываешь ошибки.

**PR ревью:** P1. **Защита:** `@Composable`, `LazyColumn`, `AlertDialog`, `TextField`, State локальной формы, callback.

**Порядок работы.** Возьми шаблон P3 из `prompts/ROLE_PROMPTS.md` и составь план для одной задачи. Работай в своей ветке и передай PR на ревью.
