package com.example.matchday.presentation.ui.teams

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.matchday.domain.model.Team
import com.example.matchday.presentation.ui.theme.MatchdayTheme

@Preview(name = "Teams - empty", widthDp = 320, heightDp = 568)
@Composable
private fun TeamsEmptyPreview() {
    MatchdayTheme {
        TeamsScreen(
            tournamentName = "SDU FIFA Cup",
            teams = emptyList(),
            onAddTeam = { Result.failure(IllegalArgumentException()) },
            onBack = {}
        )
    }
}

@Preview(name = "Teams - small screen", widthDp = 320, heightDp = 568)
@Preview(name = "Teams - large font", widthDp = 320, heightDp = 568, fontScale = 1.5f)
@Composable
private fun TeamsPopulatedPreview() {
    MatchdayTheme {
        TeamsScreen(
            tournamentName = "International Student Football Championship Autumn Season",
            teams = listOf(
                Team("preview-1", "preview-cup", "Arsenal"),
                Team("preview-2", "preview-cup", "Westminster Wolverhampton Wanderers Club"),
                Team("preview-3", "preview-cup", "Chelsea"),
                Team("preview-4", "preview-cup", "University Football Champions United")
            ),
            onAddTeam = { Result.failure(IllegalArgumentException()) },
            onBack = {}
        )
    }
}
