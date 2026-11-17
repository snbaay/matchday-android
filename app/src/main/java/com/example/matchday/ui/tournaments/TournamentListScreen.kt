package com.example.matchday.ui.tournaments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchday.model.Tournament

@Composable
fun TournamentListScreen(
    tournaments: List<Tournament>,
    onCreateTournament: (String) -> Result<String>,
    onOpenTournament: (String) -> Unit
) {
    // onCreateTournament will be used by the create dialog in P4-05.
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Tournaments",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            items(tournaments, key = { it.id }) { tournament ->
                TournamentCard(
                    tournament = tournament,
                    onOpenTournament = onOpenTournament
                )
            }
        }
    }
}
