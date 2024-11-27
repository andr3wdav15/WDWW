package com.example.wdww.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wdww.model.MediaItem

@Composable
fun MediaCard(
    mediaItem: MediaItem,
    onClick: (MediaItem) -> Unit,
    badge: String? = null
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick(mediaItem) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
            Column {
                // Poster Image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            if (!mediaItem.posterPath.isNullOrEmpty()) {
                                "https://image.tmdb.org/t/p/w342${mediaItem.posterPath}"
                            } else {
                                "https://via.placeholder.com/342x513.png?text=No+Poster"
                            }
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Title
                Text(
                    text = mediaItem.title ?: mediaItem.name ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Badge (if provided)
            badge?.let {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
