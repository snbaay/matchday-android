# Шаблоны для ролей команды

Каждый участник работает в своей Git-ветке и рабочей копии. Перед началом убедитесь, что PR P1 foundation уже в `main`. Возьмите шаблон своей роли и замените `P?-??` на ID одной конкретной задачи.

## Universal opening (сначала отправить этот текст)

```text
You are working in the root of our Android MATCHDAY Git repository.
First, READ: CONTRIBUTING.md, START_HERE.md, docs/01_SCOPE.md, docs/02_ARCHITECTURE.md,
docs/03_CONTRACTS.md, docs/04_TEAM_TASKS.md, docs/05_GIT_WORKFLOW.md,
docs/06_DESIGN_SYSTEM.md, docs/07_QA_DEFENSE.md, and my PERSON file in tasks/.
Do not code yet. Tell me which branch we are on, what the app does, our Week 12 scope,
what my role is allowed to edit, exact contracts to follow, and the next task's acceptance criteria.
If you cannot access files or git, say so, do not guess. Wait for my approval of your plan.
```

## P1 — Team Lead / Architecture

```text
I am PERSON 1 (P1), team lead. My current task is P1-?? from docs/04_TEAM_TASKS.md.
Use CONTRIBUTING.md and all docs as the source of truth. Stay strictly inside P1's owned paths.
Do NOT modify screens owned by P2/P3/P4. We are using Kotlin, Jetpack Compose,
MVVM, a single AppViewModel, StateFlow and in-memory repository only for Week 12.
Before coding, give a short plan: exact file paths, method signatures, tests,
commands, and what must remain untouched. Wait for approval.
After implementation: inspect git diff, compile/test if tools allow, report actual results.
With my explicit permission only, create a feat/p1-* branch, commit meaningful changes,
push that feature branch, and create a PR to main if GitHub credentials/tools allow.
NEVER merge PRs, push directly to main, or use force push.
```

## P2 — Matches / Standings

```text
I am PERSON 2 (P2), responsible ONLY for matches, score input and standings.
My task ID is P2-?? in docs/04_TEAM_TASKS.md.
Allowed files: ui/matches/**, ui/standings/**, domain/StandingsCalculator.kt,
and tests in matching P2-owned packages. Read docs/03_CONTRACTS.md carefully.
Do NOT edit AppViewModel, models, navigation, Gradle, UI theme or P3/P4 files.
Screens must receive state/callbacks through the exact fixed signatures.
Standings: 3 points win, 1 draw, 0 loss; sorted by points, goal difference, goals for, name.
Never double-count edited scores. No persistent storage or backend.
Before coding propose exact files and checks, then wait for approval.
After changes report the real build/test results, review the diff.
With permission you may branch/commit/push/create PR but NEVER merge to main.
If a core contract must change, stop and report to P1.
```

## P3 — Teams / UI Components

```text
I am PERSON 3 (P3), responsible ONLY for Teams UI and reusable UI components.
My task ID is P3-?? in docs/04_TEAM_TASKS.md.
Allowed files: ui/teams/**, ui/components/** and matching P3-owned tests.
Read docs/03_CONTRACTS.md and honor TeamsScreen's exact function signature.
Use Compose Material 3, LazyColumn, AlertDialog, TextField; use callbacks and
Result<String>. Handle blank/duplicate team names visibly; do not alter repository.
Do NOT edit navigation, ViewModel, models, Gradle, theme, Matches or Tournaments.
Give a simple plan with exact files and expected UI behavior first; wait for approval.
Make code easy for a beginner to explain. Run available checks and report truthfully.
With permission you may create a P3 feature branch, commit, push, open PR.
Never merge or push to main. If your task requires a foreign file, stop and ask P1.
```

## P4 — Tournaments / UI Theme

```text
I am PERSON 4 (P4), responsible ONLY for the tournament list, tournament overview
and Material 3 theme. My task ID is P4-?? from docs/04_TEAM_TASKS.md.
Allowed files: ui/tournaments/**, ui/theme/** and matching P4-owned tests.
Read docs/03_CONTRACTS.md and docs/06_DESIGN_SYSTEM.md.
Create simple, clean, readable UI. The tournament list creates a tournament with
Result<String> callback; dashboard shows live team/match counts and 3 buttons.
Do NOT change repository, ViewModel, navigation, Gradle or other members' files.
First give me a plan (exact files, contracts, tests), wait for approval.
After implementation inspect git diff and run available checks. Report any limits.
With permission you may create a P4 feature branch, commit, push, open PR.
Never merge to main or force-push. If blocked by a core issue, report to P1.
```

## After coding: universal review request

```text
Please review only my branch diff against main. Check:
1) changed files belong to my role;
2) code matches docs/03_CONTRACTS.md exactly;
3) no fake data substituted for app state;
4) no duplicate standings calculations or input crashes;
5) build/tests were actually executed (say if unavailable);
6) no unrelated refactoring or secrets.
List any risks and explain what this code does in simple Russian so I can defend it.
Do not merge anything. Do not make new edits until I approve them.
```

## Short recovery prompt after a problem

```text
Stop changing files. Show exact error, file path and owning person, what you changed,
and whether you were outside my allowed scope. Suggest the smallest fix.
Do not touch another owner's files or change dependency versions. I will coordinate.
```
