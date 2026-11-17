package com.example.matchday.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matchday.model.Match
import com.example.matchday.ui.theme.MatchdayTheme

/**
 * Displays team names and two numeric score inputs for entering or editing a match score.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    match: Match,
    homeTeamName: String,
    awayTeamName: String,
    onSaveScore: (Int, Int) -> Result<Unit>, // Validation and saving will be wired in P2-07.
    onBack: () -> Unit
) {
    var homeScoreInput by rememberSaveable(match.id, match.homeScore) {
        mutableStateOf(match.homeScore?.toString().orEmpty())
    }
    var awayScoreInput by rememberSaveable(match.id, match.awayScore) {
        mutableStateOf(match.awayScore?.toString().orEmpty())
    }
    val hasExistingScore = match.homeScore != null && match.awayScore != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Edit score",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$homeTeamName vs $awayTeamName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (hasExistingScore) {
                            "Editing finished match (${match.homeScore} - ${match.awayScore})"
                        } else {
                            "Enter match score (0 to 99)"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ScoreInputRow(
                        teamName = homeTeamName,
                        roleLabel = "Home team",
                        scoreText = homeScoreInput,
                        onScoreChange = { homeScoreInput = it }
                    )

                    ScoreInputRow(
                        teamName = awayTeamName,
                        roleLabel = "Away team",
                        scoreText = awayScoreInput,
                        onScoreChange = { awayScoreInput = it }
                    )
                }
            }

            Button(
                onClick = { /* Save validation and Result handling will be added in P2-07. */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save score")
            }
        }
    }
}

@Composable
private fun ScoreInputRow(
    teamName: String,
    roleLabel: String,
    scoreText: String,
    onScoreChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = teamName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = roleLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            value = scoreText,
            onValueChange = onScoreChange,
            modifier = Modifier.width(104.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "0",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScoreScreenPlannedPreview() {
    MatchdayTheme {
        ScoreScreen(
            match = Match(
                id = "m1",
                tournamentId = "t1",
                homeTeamId = "team1",
                awayTeamId = "team2"
            ),
            homeTeamName = "Arsenal",
            awayTeamName = "Liverpool",
            onSaveScore = { _, _ -> Result.success(Unit) },
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScoreScreenFinishedPreview() {
    MatchdayTheme {
        ScoreScreen(
            match = Match(
                id = "m2",
                tournamentId = "t1",
                homeTeamId = "team3",
                awayTeamId = "team4",
                homeScore = 3,
                awayScore = 1
            ),
            homeTeamName = "Real Madrid",
            awayTeamName = "Barcelona",
            onSaveScore = { _, _ -> Result.success(Unit) },
            onBack = {}
        )
    }
}
