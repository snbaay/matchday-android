package com.example.matchday.presentation.ui.matches

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
import androidx.compose.ui.res.stringResource
import com.example.matchday.R
import com.example.matchday.presentation.extension.errorText
import com.example.matchday.domain.model.Match
import com.example.matchday.presentation.ui.theme.MatchdayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    match: Match,
    homeTeamName: String,
    awayTeamName: String,
    onSaveScore: (Int, Int) -> Result<Unit>,
    onBack: () -> Unit
) {
    var homeScoreInput by rememberSaveable(match.id, match.homeScore) {
        mutableStateOf(match.homeScore?.toString() ?: "")
    }
    var awayScoreInput by rememberSaveable(match.id, match.awayScore) {
        mutableStateOf(match.awayScore?.toString() ?: "")
    }
    var errorMessage by rememberSaveable(match.id) {
        mutableStateOf<Int?>(null)
    }
    var hasAttemptedSave by rememberSaveable(match.id) {
        mutableStateOf(false)
    }

    val hasExistingScore = match.homeScore != null && match.awayScore != null
    val isHomeInvalid = hasAttemptedSave && !isValidScoreText(homeScoreInput)
    val isAwayInvalid = hasAttemptedSave && !isValidScoreText(awayScoreInput)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.edit_score),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.score_teams, homeTeamName, awayTeamName),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.back))
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
                            stringResource(R.string.editing_finished_match, match.homeScore, match.awayScore)
                        } else {
                            stringResource(R.string.enter_match_score)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ScoreInputRow(
                        teamName = homeTeamName,
                        roleLabel = stringResource(R.string.home_team),
                        scoreText = homeScoreInput,
                        isError = isHomeInvalid,
                        onScoreChange = {
                            homeScoreInput = it
                            errorMessage = null
                        }
                    )

                    ScoreInputRow(
                        teamName = awayTeamName,
                        roleLabel = stringResource(R.string.away_team),
                        scoreText = awayScoreInput,
                        isError = isAwayInvalid,
                        onScoreChange = {
                            awayScoreInput = it
                            errorMessage = null
                        }
                    )
                }
            }

            val activeError = errorMessage
            if (activeError != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Text(
                        text = stringResource(activeError),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    hasAttemptedSave = true
                    val validationError = validateScoreInputs(homeScoreInput, awayScoreInput)
                    val homeScore = homeScoreInput.trim().toIntOrNull()
                    val awayScore = awayScoreInput.trim().toIntOrNull()
                    if (validationError != null) {
                        errorMessage = validationError
                    } else if (homeScore != null && awayScore != null) {
                        val result = onSaveScore(homeScore, awayScore)
                        if (result.isSuccess) {
                            errorMessage = null
                            onBack()
                        } else {
                            errorMessage = result.exceptionOrNull().errorText()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_score))
            }
        }
    }
}

private fun isValidScoreText(input: String): Boolean {
    val score = input.trim().toIntOrNull() ?: return false
    return score in 0..99
}

private fun validateScoreInputs(homeInput: String, awayInput: String): Int? {
    val homeTrimmed = homeInput.trim()
    val awayTrimmed = awayInput.trim()
    if (homeTrimmed.isEmpty() || awayTrimmed.isEmpty()) {
        return R.string.score_both_required
    }
    val homeScore = homeTrimmed.toIntOrNull()
    val awayScore = awayTrimmed.toIntOrNull()
    if (homeScore == null || awayScore == null) {
        return R.string.score_whole_numbers
    }
    if (homeScore !in 0..99 || awayScore !in 0..99) {
        return R.string.score_range
    }
    return null
}

@Composable
private fun ScoreInputRow(
    teamName: String,
    roleLabel: String,
    scoreText: String,
    isError: Boolean,
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
            isError = isError,
            placeholder = {
                Text(
                    text = stringResource(R.string.score_placeholder),
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
