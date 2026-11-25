package com.example.matchday.presentation.ui.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.matchday.R
import com.example.matchday.domain.model.Club
import com.example.matchday.presentation.ui.components.ClubBadge

@Composable
internal fun ClubResultRow(
    club: Club,
    onSaveClub: ((Club) -> Unit)?,
    isSaved: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClubBadge(badgeUrl = club.badgeUrl)
        Text(text = club.name, modifier = Modifier.weight(1f))
        if (onSaveClub != null) {
            val saveClubLabel = stringResource(R.string.club_search_save_club, club.name)
            TextButton(
                onClick = { onSaveClub(club) },
                enabled = !isSaved,
                modifier = Modifier.semantics { contentDescription = saveClubLabel }
            ) {
                Text(stringResource(if (isSaved) R.string.saved else R.string.save))
            }
        }
    }
}
