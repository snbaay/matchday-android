# AGENTS.md — MATCHDAY repository instructions

Read this file **before any code change**. The project is a beginner-friendly Android Week 12 group assignment. Keep implementations small, explainable, buildable, and within the user's assigned ownership.

## Mandatory source documents

Before writing code, read: `docs/01_SCOPE.md`, `docs/02_ARCHITECTURE.md`, `docs/03_CONTRACTS.md`, `docs/04_TEAM_TASKS.md`, `docs/05_GIT_CODEX_WORKFLOW.md`. For visuals read `docs/06_DESIGN_SYSTEM.md`; for checks read `docs/07_QA_DEFENSE.md`. The user's prompt specifies person P1/P2/P3/P4 and issue ID. Read that person's own file in `tasks/` and only the task(s) for that role.

## Non-negotiable rules

1. **Never modify `main` directly. Never merge a PR.** Work on a role/task-specific feature branch; create PR only when explicitly permitted. A human reviewer merges after checking.
2. **Never modify files outside the role's allowed paths** in `docs/04_TEAM_TASKS.md`, even to "quickly fix" a compile error. If cross-role changes are necessary, stop and request coordination. P1 owns integration files.
3. Never force-push, rewrite another person's commits, delete other members' files, change collaborators, or alter GitHub security/settings without explicit approval.
4. Never make fake or empty commits. A commit must contain a coherent, meaningful and reviewed change. Do not falsify authorship or use another student's Git identity.
5. No Firebase, backend, networking, Room, Hilt, multi-module setup, auth, payments, camera, or additional libraries without explicit approval. Use the Android Studio-generated versions/dependencies; add only necessary official AndroidX dependencies.
6. Week 12 stores data **in memory only**. Do not claim persistence across process restarts. Work within the specified tournament/teams/matches/standings MVP.
7. Kotlin, Jetpack Compose + Material 3, ViewModel, StateFlow, Navigation Compose. One `MainActivity`, one shared `AppViewModel`, one single-source-of-truth `InMemoryTournamentRepository`. Screen composables receive data and callbacks. Do not introduce a second state owner for tournaments/teams/matches.
8. IDs passed via navigation (`tournamentId`, `matchId`). Never pass entire object instances as navigation arguments. Back navigation must work.
9. Recompute standings from finished matches, **never increment counters** on save. An edited score must not double-count a match. Rules: W=3, D=1, L=0; sort points desc, goal difference desc, goals for desc, name asc.
10. Validate user input. Tournament/team names cannot be blank. Duplicate team names (trim + case-insensitive) in the same tournament are forbidden. Home and away team must differ and belong to the same tournament. Scores must be integers 0..99.
11. Do not claim a successful build/test when you have not run it. If the Android SDK, emulator, or GitHub permissions are missing, report the exact limitation.
12. Prioritize simple Kotlin code, readable names, correct state updates, and basic tests. No "clever" architecture, feature creep, animations, or cosmetic refactoring outside scope.
13. Keep secrets out of Git. Never commit `local.properties`, SDK paths, credentials, tokens, signing keys, or `.env`.
14. Before coding, output a short plan: role, task ID, exact files to create/change, expected tests. After coding, summarize files changed, behavior, test results, and any unresolved issues.

## Ownership

- **P1 / Team lead:** Gradle/project config, `MainActivity.kt`, `model/**`, `data/**`, `viewmodel/**`, `navigation/**`, root documentation and root `AGENTS.md`.
- **P2 / Matches:** `ui/matches/**`, `ui/standings/**`, `domain/StandingsCalculator.kt`, matching tests in owned test packages.
- **P3 / Teams:** `ui/teams/**`, `ui/components/**`, matching tests in owned test packages.
- **P4 / Tournaments & design:** `ui/tournaments/**`, `ui/theme/**`, matching tests in owned test packages.

## Verification

On Windows: `./gradlew.bat :app:assembleDebug` and `./gradlew.bat :app:testDebugUnitTest`; when a device/emulator is available, manually exercise the modified screen. On macOS/Linux use `./gradlew`.

## Pull request checklist

- Only owned files changed (unless P1 integration and explicitly approved).
- All task acceptance criteria met; no unrelated formatting/restructuring.
- Build/test results stated accurately; tested on emulator if available.
- Code is explained in plain language; author can defend it.
- Do not use `git push --force` or merge directly into `main`.
