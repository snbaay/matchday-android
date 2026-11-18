# 02 — Архитектура Android / MVVM

> Эта схема отражает исходный контракт Week 12. Текущая структура domain/data/presentation и DI описана в `09_CLEAN_ARCHITECTURE.md`.

## Главный принцип

**Одна Activity, один shared ViewModel, один repository — для Week 12 достаточно.** Не создавать пять отдельных ViewModel, Hilt и модульную архитектуру ради сложности.

```text
MainActivity
  └── AppNavGraph (Navigation Compose)
       ├── Tournaments UI / Dashboard UI (P4)
       ├── Teams UI + common components (P3)
       ├── Matches UI + Standings UI (P2)
       └── Score UI (P2)
             ▲ данные и пользовательские действия
             │
        AppViewModel (P1)
             ▲ val state: StateFlow<AppData>
             │
        TournamentRepository (P1)
             └── InMemoryTournamentRepository (P1)
                   ├── List<Tournament>
                   ├── List<Team>
                   └── List<Match>

StandingsCalculator (P2): чистая Kotlin-функция для расчёта итоговой таблицы.
Вызывается через AppViewModel после интеграции P1.
```

## Папки и владельцы

```text
app/src/main/java/com/example/matchday/
  MainActivity.kt                         # P1
  model/
    Tournament.kt                         # P1
    Team.kt                               # P1
    Match.kt                              # P1
    StandingRow.kt                        # P1
    AppData.kt                             # P1
  data/
    TournamentRepository.kt               # P1
    InMemoryTournamentRepository.kt       # P1
  viewmodel/
    AppViewModel.kt                        # P1
  navigation/
    Routes.kt                              # P1
    AppNavGraph.kt                         # P1
  domain/
    StandingsCalculator.kt                # P2
  ui/
    tournaments/
      TournamentListScreen.kt             # P4
      TournamentDashboardScreen.kt        # P4
      TournamentCard.kt                   # P4
    teams/
      TeamsScreen.kt                      # P3
      TeamRow.kt                          # P3
    components/
      EmptyState.kt                       # P3
      AppTopBar.kt                        # P3
    matches/
      MatchesScreen.kt                    # P2
      MatchRow.kt                         # P2
      ScoreScreen.kt                      # P2
    standings/
      StandingsScreen.kt                  # P2
    theme/
      Color.kt, Theme.kt, Type.kt         # P4
```

`src/test/java/...` повторяет те же ownership-папки: модель/репозиторий тестирует P1, калькулятор — P2, Compose screen tests по возможности — их авторы. Реальное имя пакета при создании проекта должно быть `com.example.matchday`.

## Модели Kotlin (концептуальный контракт)

```kotlin
data class Tournament(val id: String, val name: String)
data class Team(val id: String, val tournamentId: String, val name: String)
data class Match(
    val id: String,
    val tournamentId: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val homeScore: Int? = null,
    val awayScore: Int? = null
)
data class StandingRow(
    val teamId: String,
    val teamName: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}
data class AppData(
    val tournaments: List<Tournament> = emptyList(),
    val teams: List<Team> = emptyList(),
    val matches: List<Match> = emptyList()
)
```

Не добавлять Android `Context` в модели. ID создаются `UUID.randomUUID().toString()`. Не хранить в модели дублированные счётчики таблицы.

## Контракт repository и ViewModel

Repository владеет `MutableStateFlow<AppData>` (приватно), наружу отдаёт `StateFlow<AppData>`. Обновляет state заменой **нового immutable списка**, не мутирует старый список на месте.

Публичные методы `AppViewModel` (обязательно одинаковые имена в коде и UI-контрактах):

```kotlin
val state: StateFlow<AppData>
fun createTournament(name: String): Result<String>                 // созданный ID
fun addTeam(tournamentId: String, name: String): Result<String>    // созданный ID
fun createMatch(tournamentId: String, homeTeamId: String, awayTeamId: String): Result<String>
fun saveScore(matchId: String, homeScore: Int, awayScore: Int): Result<Unit>
fun standingsFor(tournamentId: String): List<StandingRow>
```

Репозиторий предоставляет соответствующие методы, кроме `standingsFor`; её реализация в VM делегирует `StandingsCalculator.calculate(teamsOfTournament, finishedMatchesOfTournament)`. До PR P2 в foundation у VM может быть временный честный заглушечный результат `emptyList()`, который **обязательно заменяется** при интеграции.

Исключения/ошибки: для невалидного ввода `Result.failure(IllegalArgumentException("..."))`; `Result.success` при успешной операции. Экран показывает понятный текст ошибки. Ни один `Result.failure` не должен вызывать crash. Поддерживать однообразно во всех методах.

## StateFlow и UI

- `AppNavGraph` получает **один** `AppViewModel` от `MainActivity`/App root.
- Собирает `val data by appViewModel.state.collectAsStateWithLifecycle()` в composable; интерфейс обновляется автоматически.
- Экраны получают **значения** и **callback**, не содержат ссылок на repository и не создают ViewModel.
- Поля формы, диалоги и сообщение о валидации можно хранить локальным `rememberSaveable`, но сами команды/матчи/турниры хранятся только в repository.
- `rememberSaveable` не гарантирует сохранность списка турниров между перезапусками: не пытайтесь использовать его вместо repository.

## Алгоритм таблицы

1. Создать `StandingRow` с нулями для каждой команды турнира.
2. Пройти по матчам, где **оба** счёта не `null`.
3. Увеличить played, goalsFor/goalsAgainst для обеих команд.
4. При `homeScore > awayScore` home.won++, away.lost++, home.points+=3; при равенстве обеим drawn++, points+=1; иначе наоборот.
5. Отсортировать по `points desc`, `goalDifference desc`, `goalsFor desc`, `teamName asc`.
6. При повторном сохранении результата **не прибавлять к старой таблице**, а полностью выполнить расчёт снова.

## Почему эта архитектура достаточна

Стандарты Android допускают in-memory source of truth; UI получает состояние через ViewModel, а data layer отделён от представления. Для Week 13 можно заменить реализацию repository на Room, не переписывая UI, **если** это соответствует следующему assignment.
