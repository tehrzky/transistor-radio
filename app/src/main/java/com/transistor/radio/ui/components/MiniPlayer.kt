package com.transistor.radio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.transistor.radio.domain.model.PlaybackState
import com.transistor.radio.domain.model.Station

@Composable
fun MiniPlayer(
    station: Station?,
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visible = station != null && playbackState != PlaybackState.STOPPED

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .height(72.dp)
                .zIndex(1f)
                .clickable(onClick = onTap),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 4.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ) {
            Column {
                if (playbackState == PlaybackState.BUFFERING) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Station Icon
                    AsyncImage(
                        model = station?.iconUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Text Info
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = station?.name ?: "Unknown",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (playbackState) {
                                PlaybackState.PLAYING -> "Now Playing"
                                PlaybackState.BUFFERING -> "Buffering…"
                                PlaybackState.PAUSED -> "Paused"
                                else -> ""
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }

                    // Play/Pause
                    IconButton(onClick = onPlayPause) {
                        Icon(
                            imageVector = if (playbackState == PlaybackState.PLAYING)
                                Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playbackState == PlaybackState.PLAYING)
                                "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
