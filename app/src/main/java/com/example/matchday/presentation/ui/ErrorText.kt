package com.example.matchday.presentation.ui

import androidx.annotation.StringRes
import com.example.matchday.R
import com.example.matchday.domain.model.TournamentError
import com.example.matchday.domain.model.TournamentFailure

@StringRes
fun Throwable?.errorText(): Int = when ((this as? TournamentFailure)?.error) {
    TournamentError.INVALID_TOURNAMENT_NAME -> R.string.tournament_invalid
    TournamentError.INVALID_TEAM_NAME -> R.string.team_name_invalid
    TournamentError.TOURNAMENT_NOT_FOUND -> R.string.tournament_not_found
    TournamentError.DUPLICATE_TEAM -> R.string.team_duplicate
    TournamentError.SAME_TEAM -> R.string.same_team
    TournamentError.WRONG_TOURNAMENT -> R.string.wrong_tournament
    TournamentError.DUPLICATE_MATCH -> R.string.duplicate_match
    TournamentError.INVALID_SCORE -> R.string.score_invalid
    TournamentError.MATCH_NOT_FOUND -> R.string.match_not_found
    null -> R.string.operation_failed
}
