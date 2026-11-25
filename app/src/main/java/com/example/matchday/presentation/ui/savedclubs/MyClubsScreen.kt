package com.example.matchday.presentation.ui.savedclubs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.matchday.R
import com.example.matchday.domain.model.SavedClub
import com.example.matchday.presentation.ui.components.AppTopBar
import com.example.matchday.presentation.ui.components.EmptyState
import com.example.matchday.presentation.viewmodel.SavedClubsUiState

@Composable
internal fun MyClubsScreen(
    state: SavedClubsUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(title = stringResource(R.string.my_clubs), onBack = onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                SavedClubsUiState.Loading -> item { CircularProgressIndicator() }
                SavedClubsUiState.Error -> item {
                    Text(stringResource(R.string.saved_clubs_load_failed), color = MaterialTheme.colorScheme.error)
                }
                is SavedClubsUiState.Content -> {
                    if (state.clubs.isEmpty()) {
                        item { EmptyState(message = stringResource(R.string.saved_clubs_empty)) }
                    }
                    items(state.clubs, key = { it.id }) { club ->
                        SavedClubCard(club)
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedClubCard(club: SavedClub) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(club.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}
