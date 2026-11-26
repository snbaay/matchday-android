# MATCHDAY — Local Tournament Manager

Учебное Android-приложение для организации футбольных/FIFA-турниров, поиска реальных футбольных клубов через публичный REST API и ведения локального списка избранных клубов «Мои клубы».

> **Хранение данных:** сохранённые клубы и заметки хранятся в локальной БД **Room** (`saved_clubs.db`), выбранный порядок сортировки — в **Preferences DataStore** (`saved_club_preferences`), и оба сохраняются после полного закрытия приложения. Турниры, команды турнира и матчи хранятся в памяти процесса (`InMemoryTournamentDataSource`).

## Команда

- **Yessenbay (P1)** — Clean Architecture, Koin DI, `AppViewModel`, Type-Safe Navigation, сетевой слой поиска клубов через OpenLigaDB (`ClubSearchApi`, `RemoteClubSearchRepository`) и интеграция экранов.
- **Dulat (P2)** — матчи, ввод счёта и расчёт турнирной таблицы (`MatchesScreen`, `ScoreScreen`, `StandingsScreen`, `CalculateStandingsUseCase`), локальное хранилище **Room** для сохранённых клубов (`SavedClubEntity`, `SavedClubDao`, `SavedClubsDatabase`, `LocalSavedClubsRepository`).
- **Elvina (P3)** — экран команд и общие компоненты (`TeamsScreen`, `TeamRow`, `AppTopBar`, `EmptyState`), экран «Мои клубы» (`MyClubsScreen`, `SavedClubsViewModel`), редактирование заметок, удаление и сохранение сортировки в **Preferences DataStore** (`DataStoreClubSortPreferences`).
- **Nazerke (P4)** — экраны турниров и Material 3 тема (`TournamentListScreen`, `TournamentDashboardScreen`, `Color`, `Type`, `Theme`), состояния поиска клубов и Retry (`ClubSearchViewModel`, `ClubSearchItems`), загрузка эмблем через **Coil** (`ClubResultRow`, `ClubBadge`).

## Возможности

1. **Турниры, команды, матчи и таблица:**
   - создание турниров (1..60 символов) и добавление команд (1..40 символов, без дубликатов регистра) вручную или напрямую из «Моих клубов»;
   - создание матчей между разными командами, ввод и редактирование счёта (`0..99`);
   - автоматический пересчёт таблицы с нуля по завершённым матчам (`3 / 1 / 0` очков, сортировка: очки ↓ → разница голов ↓ → забитые голы ↓ → название ↑).
2. **Поиск футбольных клубов (OpenLigaDB + Retrofit + Coil):**
   - поиск клубов АПЛ, Бундеслиги, Лиги чемпионов и Лиги Европы из корутины через `SearchClubsUseCase`;
   - состояния `Loading`, `Success`, `Empty`, `Error` с повтором последнего запроса (`Retry`) и отображением эмблем клубов через `Coil`.
3. **«Мои клубы» (Room + Flow + Preferences DataStore):**
   - сохранение выбранных клубов в локальную базу Room, реактивное обновление списка через `Flow`;
   - добавление/редактирование заметок и удаление клубов по первичному ключу `id`;
   - переключение сортировки `A–Z` / `Z–A` с сохранением выбора в `Preferences DataStore` между перезапусками приложения и быстрое добавление сохранённых клубов в активный турнир.

## Архитектура (Clean Architecture + MVVM)

```text
Compose Screens → ViewModels (StateFlow) → Domain Use Cases → Repository Interfaces
                                                                     ↑
                       ┌────────────────────────────┬────────────────┴───────────────┐
         InMemoryTournamentRepository   RemoteClubSearchRepository     LocalSavedClubsRepository
         (InMemoryTournamentDataSource)  (Retrofit + OpenLigaDB)       (Room + Preferences DataStore)
```

### Основные пакеты (`app/src/main/java/com/example/matchday/`)

- **`domain/`** — чистые модели (`Tournament`, `Team`, `Match`, `StandingRow`, `Club`, `SavedClub`, `ClubSortOrder`), контракты репозиториев и use cases (`TournamentUseCases`, `StandingsCalculator`, `SearchClubsUseCase`, `SavedClubUseCases`).
- **`data/remote/`** — `ClubSearchApi` (`OPENLIGA_BASE_URL`) и `ClubDto` (Retrofit + Kotlinx Serialization).
- **`data/local/`** — `SavedClubEntity`, `SavedClubDao`, `SavedClubsDatabase` (Room), `InMemoryTournamentDataSource` и `TournamentSnapshotDto`.
- **`data/repository/`** — реализации репозиториев (`InMemoryTournamentRepository`, `RemoteClubSearchRepository`, `LocalSavedClubsRepository`, `DataStoreClubSortPreferences`).
- **`data/mapper/`** — мапперы между DTO/Entity и доменными моделями (`ClubMapper`, `SavedClubMapper`, `TournamentMappers`).
- **`data/extension/` и `presentation/extension/`** — extension-свойства и функции (`ContextDataStoreExtensions`, `ThrowableExtensions`).
- **`di/`** — Koin-модули (`dataModule`, `domainModule`, `presentationModule`) в `AppModules.kt` и `MatchdayApplication.kt`.
- **`presentation/`** — 7 типобезопасных `@Serializable` маршрутов (`TournamentsRoute`, `MyClubsRoute`, `TournamentRoute`, `TeamsRoute`, `MatchesRoute`, `StandingsRoute`, `ScoreRoute`), `AppNavGraph`, ViewModels (`AppViewModel`, `ClubSearchViewModel`, `SavedClubsViewModel`) и Compose-экраны.

## Технологии

- Kotlin, Coroutines & `Flow` / `StateFlow`
- Jetpack Compose + Material 3 + Type-Safe Navigation Compose
- Clean Architecture + MVVM + Koin (`koin-android`, `koin-compose-viewmodel`)
- Retrofit 3 + Kotlinx Serialization (OpenLigaDB REST API)
- Coil 3 (`coil-compose`, `coil-network-okhttp`, `coil-svg`)
- Room (`2.8.5` + KSP) и Preferences DataStore (`1.1.7`)
- JUnit 4, Coroutines Test и Compose / Android Instrumentation Tests

## Источник данных для поиска клубов

[OpenLigaDB](https://api.openligadb.de/index.html) предоставляет каталоги клубов без регистрации. MATCHDAY загружает каталоги АПЛ, Бундеслиги и Лиги Европы сезона 2026/27 и Лиги чемпионов сезона 2025/26, объединяя дубликаты по `teamId` и сопоставляя запросы без учёта регистра и диакритических знаков. Данные предоставлены [OpenLigaDB по лицензии ODbL 1.0](https://www.openligadb.de/en/license).

## Запуск и проверка

1. Откройте проект в Android Studio и дождитесь завершения Gradle Sync.
2. Запустите конфигурацию `app` на Android Emulator или физическом устройстве.

Проверка сборки и тестов из терминала (Windows / PowerShell):

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

На macOS/Linux используйте `./gradlew`.
