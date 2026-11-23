# MATCHDAY — Local Tournament Manager

Учебное Android-приложение для организации небольших футбольных/FIFA-турниров. Позволяет создавать турниры, добавлять команды, расписывать матчи, записывать и редактировать счёт и просматривать автоматически рассчитанную турнирную таблицу.

> **Ограничение текущей версии:** данные хранятся только в памяти процесса (`InMemoryTournamentDataSource`) и не сохраняются после полного закрытия приложения.

## Команда

- **Yessenbay (P1)** — архитектура Clean Architecture, Koin DI, `AppViewModel`, репозиторий, Type-Safe Navigation, интеграция экранов.
- **Dulat (P2)** — матчи (`MatchRow`, `MatchesScreen`), ввод и редактирование счёта (`ScoreScreen`), расчёт и отображение таблицы (`CalculateStandingsUseCase`, `StandingsScreen`).
- **Elvina (P3)** — экран команд (`TeamsScreen`, `TeamRow`), переиспользуемые UI-компоненты (`AppTopBar`, `EmptyState`), Compose UI-тесты.
- **Nazerke (P4)** — список и обзор турниров (`TournamentListScreen`, `TournamentDashboardScreen`, `TournamentCard`), Material 3 тема и цветовая палитра (`Color`, `Type`, `Theme`).

## Возможности

1. Создание и просмотр списка турниров (валидация пустого имени, 1..60 символов).
2. Обзор выбранного турнира со счётчиками команд и матчей и быстрым переходом к разделам.
3. Добавление команд в турнир (1..40 символов, защита от дубликатов без учёта регистра).
4. Создание матчей между двумя разными командами (запрет на матч команды с самой собой и на повтор пары в рамках одного турнира).
5. Ввод и редактирование счёта матча (целые числа `0..99`, безопасная обработка некорректного ввода).
6. Автоматический пересчёт турнирной таблицы с нуля по завершённым матчам:
   - очки: победа = `3`, ничья = `1`, поражение = `0`;
   - сортировка: очки ↓ → разница мячей ↓ → забитые голы ↓ → название команды ↑;
   - незавершённые матчи (`null`) не учитываются; команды с 0 матчей отображаются с нулями; редактирование счёта (например, `3:1 → 0:0`) не приводит к двойному подсчёту.

## Архитектура (Clean Architecture + MVVM)

```text
MainActivity → Koin (AppModules) → AppViewModel (StateFlow<AppUiState>)
                                        ↓
                          Domain Use Cases & Models
             (Observe/Create/Add/Save, CalculateStandingsUseCase)
                                        ↓
                        TournamentRepository (domain interface)
                                        ↑
                     InMemoryTournamentRepository (data)
                                        ↓
                   InMemoryTournamentDataSource → DTO → Mappers

AppNavGraph (@Serializable Routes + NavigationEvent) → presentation/ui screens
```

### Основные пакеты (`app/src/main/java/com/example/matchday/`)

- **`domain/`**
  - `model/` — чистые модели (`Tournament`, `Team`, `Match`, `StandingRow`, `AppData`, `TournamentFailure`).
  - `repository/` — интерфейс `TournamentRepository`.
  - `usecase/` — бизнес-операции (`TournamentUseCases.kt`, `StandingsCalculator.kt` с `CalculateStandingsUseCase`).
- **`data/`**
  - `InMemoryTournamentRepository.kt` — реализация `TournamentRepository` со `StateFlow<AppData>`.
  - `local/` — источник данных в памяти (`InMemoryTournamentDataSource`), `TournamentSnapshotDto` и мапперы `TournamentMappers`.
- **`di/`**
  - `AppModules.kt` (`dataModule`, `domainModule`, `presentationModule`) и `MatchdayApplication.kt` — сборка зависимостей через **Koin**.
- **`presentation/`**
  - `navigation/` — 6 типобезопасных `@Serializable`-маршрутов (`TournamentsRoute`, `TournamentRoute`, `TeamsRoute`, `MatchesRoute`, `StandingsRoute`, `ScoreRoute`), `NavigationEvent` и `AppNavGraph`.
  - `viewmodel/` — единый `AppViewModel` и неизменяемый `AppUiState`.
  - `ui/` — stateless Compose-экраны (`tournaments/`, `teams/`, `matches/`, `standings/`), общие компоненты (`components/`, `ErrorText.kt`) и единая тёмная тема (`theme/`).
- **`res/values/strings.xml`** — пользовательские тексты и сообщения об ошибках.

## Технологии

- Kotlin
- Jetpack Compose + Material 3
- Clean Architecture (Domain / Data / Presentation) + MVVM
- Koin (`koin-android`)
- Type-Safe Navigation Compose (`2.10.2`, `@Serializable` + `kotlin-serialization`)
- `StateFlow` и `collectAsStateWithLifecycle`
- JUnit 4 и Compose UI Testing

## Источник данных для поиска клубов

[OpenLigaDB](https://api.openligadb.de/index.html) предоставляет каталоги клубов без регистрации. MATCHDAY загружает через Retrofit каталоги АПЛ, Бундеслиги и Лиги Европы сезона 2026/27 и Лиги чемпионов сезона 2025/26. Поиск учитывает полное и короткое название, игнорирует регистр и диакритические знаки и поднимает точные совпадения выше частичных.

Для проверки ответа API откройте [каталог Лиги чемпионов](https://api.openligadb.de/getavailableteams/ucl/2025) и найдите `FK Kairat`. Поля `teamId`, `teamName` и `teamIconUrl` преобразуются в `Club(id, name, badgeUrl)`. У клуба может отсутствовать HTTPS-ссылка на эмблему.

ViewModel может получить `SearchClubsUseCase` через Koin. Вызов `searchClubs(query)` выполняется из корутины. Пустой список означает, что совпадений нет. Сетевые ошибки передаются как `ClubSearchException`. Ошибка `RateLimited` включает `retryAfterSeconds`, если сервис прислал допустимый `Retry-After`.

Данные предоставлены [OpenLigaDB по лицензии ODbL 1.0](https://www.openligadb.de/en/license).

## Запуск и проверка

1. Откройте проект в Android Studio.
2. Дождитесь завершения Gradle Sync.
3. Выберите Android Emulator или реальное устройство и запустите конфигурацию `app`.

Проверка сборки и тестов из терминала (Windows / PowerShell):

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

На macOS/Linux используйте `./gradlew`.
