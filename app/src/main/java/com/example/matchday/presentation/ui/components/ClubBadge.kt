package com.example.matchday.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

// Wikimedia rejects requests with a generic User-Agent.
private val badgeRequestHeaders = NetworkHeaders.Builder()
    .set("User-Agent", "MATCHDAY-Android/1.0 (https://github.com/snbaay/matchday-android)")
    .build()

@Composable
internal fun ClubBadge(badgeUrl: String?, modifier: Modifier = Modifier) {
    val fallback = painterResource(R.drawable.ic_club_badge_placeholder)
    val context = LocalPlatformContext.current
    val badgeRequest = remember(context, badgeUrl) {
        ImageRequest.Builder(context)
            .data(badgeUrl)
            .httpHeaders(badgeRequestHeaders)
            .build()
    }
    Surface(
        modifier = modifier.size(44.dp),
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
}
