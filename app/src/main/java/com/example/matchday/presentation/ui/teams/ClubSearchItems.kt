package com.example.matchday.presentation.ui.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.matchday.R
import com.example.matchday.presentation.ui.components.EmptyState
import com.example.matchday.presentation.viewmodel.ClubSearchResult
import com.example.matchday.presentation.viewmodel.ClubSearchUiState

fun LazyListScope.clubSearchItems(
    state: ClubSearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit
) {
    item(key = "club-search-form") {
        ClubSearchForm(state, onQueryChange, onSearch)
    }
    when (val result = state.result) {
        ClubSearchResult.Idle -> Unit
        ClubSearchResult.Loading -> item(key = "club-search-status") {
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(
                    text = stringResource(R.string.club_search_loading),
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
        }
        ClubSearchResult.Empty -> item(key = "club-search-status") {
            EmptyState(
                message = stringResource(R.string.club_search_empty),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
        is ClubSearchResult.Error -> item(key = "club-search-status") {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = stringResource(R.string.club_search_error, result.query),
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = onRetry) {
                    Text(stringResource(R.string.club_search_retry))
                }
            }
        }
        is ClubSearchResult.Success -> items(result.clubs, key = { "club-result-${it.id}" }) { club ->
            ClubResultRow(club)
        }
    }
}

@Composable
private fun ClubSearchForm(
    state: ClubSearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val canSubmit = state.query.isNotBlank() && state.result != ClubSearchResult.Loading
    val submit = {
        if (canSubmit) {
            focusManager.clearFocus()
            onSearch()
        }
    }
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.club_search_club_name)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { submit() })
        )
        OutlinedButton(onClick = submit, enabled = canSubmit) {
            Text(stringResource(R.string.club_search_title))
        }
    }
}
