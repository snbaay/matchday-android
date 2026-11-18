package com.example.matchday.domain.model

enum class TournamentError {
    INVALID_TOURNAMENT_NAME, INVALID_TEAM_NAME, TOURNAMENT_NOT_FOUND,
    DUPLICATE_TEAM, SAME_TEAM, WRONG_TOURNAMENT, DUPLICATE_MATCH,
    INVALID_SCORE, MATCH_NOT_FOUND
}

class TournamentFailure(val error: TournamentError) : IllegalArgumentException(error.name)
