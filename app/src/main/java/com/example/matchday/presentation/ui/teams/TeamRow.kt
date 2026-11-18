package com.example.matchday.presentation.ui.teams

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchday.domain.model.Team

@Composable
fun TeamRow(
    team: Team,
    modifier: Modifier = Modifier
) {
    Text(
        text = team.name,
        modifier = modifier.padding(vertical = 8.dp),
        softWrap = true
    )
}