package com.example.matchday.presentation.ui.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.example.matchday.R
import com.example.matchday.domain.model.Club

// Wikimedia rejects requests with a generic User-Agent.
private val badgeRequestHeaders = NetworkHeaders.Builder()
    .set("User-Agent", "MATCHDAY-Android/1.0 (https://github.com/snbaay/matchday-android)")
    .build()

@Composable
internal fun ClubResultRow(club: Club) {
    val fallback = painterResource(R.drawable.ic_club_badge_placeholder)
    val context = LocalPlatformContext.current
    val badgeRequest = remember(context, club.badgeUrl) {
        ImageRequest.Builder(context)
            .data(club.badgeUrl)
            .httpHeaders(badgeRequestHeaders)
            .build()
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small
        ) {
            AsyncImage(
                model = badgeRequest,
                contentDescription = null,
                modifier = Modifier.padding(4.dp),
                placeholder = fallback,
                error = fallback,
                fallback = fallback,
                contentScale = ContentScale.Fit
            )
        }
        Text(text = club.name, modifier = Modifier.weight(1f))
    }
}
