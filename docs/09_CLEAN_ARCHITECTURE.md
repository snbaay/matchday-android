# MATCHDAY — согласованная архитектура после рефакторинга

Этот документ описывает текущий код ветки `fix/clean-architecture`. Первоначальные схемы и сигнатуры в `01`–`04` остаются историей Week 12; правила продукта и разделение ответственности участников сохраняются.

```text
MainActivity → Koin → AppViewModel → Use Cases → TournamentRepository (domain interface)
                                                 ↑
                              InMemoryTournamentRepository (data)
                                                 ↓
                         InMemoryTournamentDataSource → DTO → mappers

AppNavGraph → presentation/ui screens → callbacks → AppViewModel
```

- `domain/model`: чистые модели и коды ошибок. `domain/repository`: контракт данных. `domain/usecase`: операции и расчёт таблицы Dulat через внедрённый `CalculateStandingsUseCase`.
- `data`: реализация репозитория; `data/local`: единственный источник данных в памяти, DTO и мапперы. Данные не сохраняются после завершения процесса.
- `presentation/navigation`: шесть `@Serializable` маршрутов с передачей только ID. `presentation/viewmodel`: один общий `AppViewModel`, который зависит только от Use Cases. `presentation/ui`: Compose-экраны, локальное состояние полей и диалогов. Навигацией управляет только `AppNavGraph`.
- Koin запускается в `MatchdayApplication`; модули в `di/AppModules.kt` создают источник данных, репозиторий, Use Cases и ViewModel через конструкторы.
- Репозиторий отдаёт `StateFlow<AppData>`. ViewModel отображает его в неизменяемый `StateFlow<AppUiState>`, который `AppNavGraph` собирает через `collectAsStateWithLifecycle()`. Пустые списки показывают empty state; синхронные операции показывают результат или локализованную ошибку. Загрузки из сети или диска нет.
- События навигации заданы в `NavigationEvent` и обрабатываются внутри `AppNavGraph`. `NavController` не попадает во ViewModel.
- Пользовательские строки лежат в `res/values/strings.xml`. Ошибки передаются как `TournamentError` и преобразуются в строковые ресурсы в presentation.
- Таблица каждый раз считается заново из законченных матчей; исправление счёта не добавляет прежние очки повторно.

Проверки: `:app:assembleDebug`, `:app:testDebugUnitTest`, затем ручной сценарий из `07_QA_DEFENSE.md` на устройстве или эмуляторе.
