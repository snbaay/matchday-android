package com.example.matchday.ui.standings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matchday.model.StandingRow
import com.example.matchday.ui.theme.MatchdayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandingsScreen(
    tournamentName: String,
    rows: List<StandingRow>,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Standings",
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = tournamentName,
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
        if (rows.isEmpty()) {
            StandingsEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        StandingsHeaderRow()
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 4.dp)
                        ) {
                            itemsIndexed(
                                items = rows,
                                key = { _, row -> row.teamId }
                            ) { index, row ->
                                StandingTableRow(
                                    position = index + 1,
                                    row = row
                                )
                                if (index < rows.lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StandingsHeaderRow() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.width(24.dp)
            )
            Text(
                text = "Team",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            StatCellHeader("P", width = 24.dp)
            StatCellHeader("W", width = 24.dp)
            StatCellHeader("D", width = 24.dp)
            StatCellHeader("L", width = 24.dp)
            StatCellHeader("GD", width = 34.dp)
            StatCellHeader("Pts", width = 32.dp)
        }
    }
}

@Composable
private fun StandingTableRow(
    position: Int,
    row: StandingRow
) {
    val gdText = if (row.goalDifference > 0) {
        "+${row.goalDifference}"
    } else {
        row.goalDifference.toString()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = row.teamName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        StatCellValue(row.played.toString(), width = 24.dp)
        StatCellValue(row.won.toString(), width = 24.dp)
        StatCellValue(row.drawn.toString(), width = 24.dp)
        StatCellValue(row.lost.toString(), width = 24.dp)
        StatCellValue(gdText, width = 34.dp)
        Text(
            text = row.points.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier.width(32.dp)
        )
    }
}

@Composable
private fun StatCellHeader(
    label: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.End,
        maxLines = 1,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun StatCellValue(
    value: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.End,
        maxLines = 1,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun StandingsEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No teams in standings yet",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Add teams and enter match scores to see the tournament table.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StandingsScreenPopulatedPreview() {
    val previewRows = listOf(
        StandingRow(
            teamId = "t1",
            teamName = "Real Madrid",
            played = 1,
            won = 1,
            drawn = 0,
            lost = 0,
            goalsFor = 3,
            goalsAgainst = 1,
            points = 3
        ),
        StandingRow(
            teamId = "t2",
            teamName = "Arsenal",
            played = 0,
            won = 0,
            drawn = 0,
            lost = 0,
            goalsFor = 0,
            goalsAgainst = 0,
            points = 0
        ),
        StandingRow(
            teamId = "t3",
            teamName = "Barcelona",
            played = 1,
            won = 0,
            drawn = 0,
            lost = 1,
            goalsFor = 1,
            goalsAgainst = 3,
            points = 0
        )
    )

    MatchdayTheme {
        StandingsScreen(
            tournamentName = "SDU FIFA Cup",
            rows = previewRows,
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StandingsScreenEmptyPreview() {
    MatchdayTheme {
        StandingsScreen(
            tournamentName = "SDU FIFA Cup",
            rows = emptyList(),
            onBack = {}
        )
    }
}
