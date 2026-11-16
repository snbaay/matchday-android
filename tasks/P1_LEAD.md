# PERSON 1 — Lead, foundation, GitHub, MVVM, navigation

**Сначала:** прочитай `START_HERE.md`, `CONTRIBUTING.md`, `docs/01_SCOPE.md`, `docs/02_ARCHITECTURE.md`, `docs/03_CONTRACTS.md`, `docs/05_GIT_WORKFLOW.md`.

**Твои файлы:** `MainActivity.kt`, `model/**`, `data/**`, `viewmodel/**`, `navigation/**`, корневой Gradle/README/CONTRIBUTING/docs/.github. **Не трогай экраны и тему P2/P3/P4.**

## Чек-лист работы (полное Definition of Done см. `docs/04_TEAM_TASKS.md`)

- [ ] P1-01: Android Studio project + работающий emulator
- [ ] P1-02: GitHub + collaborators + docs + README + .gitignore
- [ ] P1-03: Navigation Compose + lifecycle Compose dependencies, Gradle Sync
- [ ] P1-04: 5 data models
- [ ] P1-05: repository + StateFlow + базовый test
- [ ] P1-06: addTeam и проверка duplicate
- [ ] P1-07: createMatch/saveScore и tests
- [ ] P1-08: AppViewModel с публичными методами из contract
- [ ] P1-09: routes and placeholder navigation, merge foundation PR
- [ ] P1-10: только после PR остальных — integration screens, calculator, QA

## Зависимости и передача работы

**Gate 1:** после P1-09 проект собирается и существует `main` с моделями/контрактами. Только тогда участники 2/3/4 начинают свои ветки.

**Gate 2:** после всех PR экранов P1 подсоединяет реальные composables в `AppNavGraph` и калькулятор в `AppViewModel`. Не исправляет чужие экраны напрямую.

**Кому отправлять на ревью:** P2. **Что защищаешь:** архитектура, репозиторий, StateFlow, MVVM, Navigation, GitHub.

**Порядок работы.** Возьми шаблон P1 из `prompts/ROLE_PROMPTS.md`, укажи один ID задачи и составь план перед изменениями. Объединение с `main` выполняет ревьюер.
