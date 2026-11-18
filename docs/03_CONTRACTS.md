# 03 — Контракты интерфейсов (API freeze v1)

> Это исходный API freeze v1. Для согласованного общего рефакторинга пути пакетов и маршруты заменены согласно `09_CLEAN_ARCHITECTURE.md`; правила данных и шесть экранов сохранены.

**Этот документ — главный способ предотвратить конфликты.** Все реализации экранов должны точно соответствовать этим именам, типам аргументов и путям. P2/P3/P4 **не меняют** `AppNavGraph.kt`, `AppViewModel.kt` или модели. Если надо изменить контракт — сначала GitHub Issue и согласование P1.

## Навигационные маршруты (владелец P1)

| Экран | Route | Кто вызывает |
|---|---|---|
| Список турниров | `tournaments` | Start destination |
| Обзор турнира | `tournament/{tournamentId}` | Из списка турниров |
| Команды | `tournament/{tournamentId}/teams` | Из обзора |
| Матчи | `tournament/{tournamentId}/matches` | Из обзора |
| Редактирование счёта | `match/{matchId}/score` | Из списка матчей |
| Таблица | `tournament/{tournamentId}/standings` | Из обзора |

`Routes.kt` содержит названия маршрутов и функции для построения route (например `Routes.tournament(id)`, `Routes.score(matchId)`). P1 задаёт точное API `Routes.kt` до PR остальных.

`AppNavGraph` должен получать ID из back-stack arguments, искать объект по ID в `state`, а при неверном ID показывать безопасный экран ошибки + Back (не crash). После успешного создания турнира разрешено оставаться на списке с новой карточкой; после успешного сохранения счёта экран `Score` закрывается через `popBackStack()`.

## Подписи composable-функций

**P4 / турниры:**

```kotlin
@Composable
fun TournamentListScreen(
    tournaments: List<Tournament>,
    onCreateTournament: (String) -> Result<String>,
    onOpenTournament: (String) -> Unit
)

@Composable
fun TournamentDashboardScreen(
    tournament: Tournament,
    teamCount: Int,
    matchCount: Int,
    onTeams: () -> Unit,
    onMatches: () -> Unit,
    onStandings: () -> Unit,
    onBack: () -> Unit
)
```

**P3 / команды:**

```kotlin
@Composable
fun TeamsScreen(
    tournamentName: String,
    teams: List<Team>,
    onAddTeam: (String) -> Result<String>,
    onBack: () -> Unit
)
```

**P2 / матчи и таблица:**

```kotlin
@Composable
fun MatchesScreen(
    tournamentName: String,
    teams: List<Team>,
    matches: List<Match>,
    onCreateMatch: (String, String) -> Result<String>,
    onOpenScore: (String) -> Unit,
    onBack: () -> Unit
)

@Composable
fun ScoreScreen(
    match: Match,
    homeTeamName: String,
    awayTeamName: String,
    onSaveScore: (Int, Int) -> Result<Unit>,
    onBack: () -> Unit
)

@Composable
fun StandingsScreen(
    tournamentName: String,
    rows: List<StandingRow>,
    onBack: () -> Unit
)
```

**P2 / чистая функция расчёта:**

```kotlin
object StandingsCalculator {
    fun calculate(teams: List<Team>, matches: List<Match>): List<StandingRow>
}
```

Уже отфильтрованные по турниру списки передаёт P1. Функция дополнительно безопасно игнорирует незаконченные матчи и пары с неизвестными командами. Никаких `Context`, Compose или repository внутри калькулятора.

## UI-поведение и ответственность

| Экран | Кто отвечает за валидацию | Кто меняет данные |
|---|---|---|
| Создать турнир | P4 — сообщение пользователю; P1 — реальное правило в репозитории | P1 AppViewModel callback |
| Добавить команду | P3 — поле/ошибка; P1 — дубликат/пустое имя в репозитории | P1 AppViewModel callback |
| Создать матч | P2 — выбор двух разных команд и предупреждение; P1 — окончательная проверка | P1 AppViewModel callback |
| Ввести счёт | P2 — поля только с числами, 0..99; P1 — окончательная проверка | P1 AppViewModel callback |
| Таблица | P2 — отображение; P2 calculator; P1 — передача результата | Не изменяет данные |

Текст пользовательских ошибок допускается редактировать, **типы методов/пакетов/путей — нет** без согласования.

## Примеры вызовов для экрана

```kotlin
val result = onAddTeam(input.trim())
if (result.isSuccess) {
    // Закрыть диалог, очистить поле; новый team появится через StateFlow.
} else {
    // Показать result.exceptionOrNull()?.message ?: "Cannot add team"
}
```

При `onSaveScore(...).isSuccess` вызвать `onBack()`; при failure показывать ошибку и оставаться на экране. Никакого фиктивного обновления списка напрямую из UI.

## Правило изменения контрактов

1. Участник описывает необходимость в Issue/комментарии PR.
2. P1 одобряет или предлагает решение без изменения интерфейса.
3. Если изменение нужно, P1 обновляет `docs/03_CONTRACTS.md` отдельным маленьким PR и предупреждает всех.
4. Только после его merge остальные используют новый контракт.
