package com.example.wdww.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wdww.model.MediaItem
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel

@SuppressLint("DefaultLocale")
@Composable
fun MediaItemCard(
    mediaItem: MediaItem,
    showGenre: Boolean = false,
    showTypeWithGenre: Boolean = false,
    isFavoritesList: Boolean = false,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    var showModal by remember { mutableStateOf(false) }
    rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                Log.d("MediaItemCard", "Card clicked: ${mediaItem.name ?: mediaItem.title} (ID: ${mediaItem.id}, Type: ${mediaItem.mediaType})")
                showModal = true
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Poster Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (!mediaItem.posterPath.isNullOrEmpty()) {
                            "https://image.tmdb.org/t/p/w200${mediaItem.posterPath}"
                        } else {
                            "https://via.placeholder.com/200x300.png?text=No+Poster"
                        }
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = mediaItem.title ?: mediaItem.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Media Type and Genre
                    Text(
                        text = if (showGenre && !mediaItem.genreIds.isNullOrEmpty()) {
                            if (showTypeWithGenre) {
                                // For trending screen, show type and first genre
                                val type = when (mediaItem.mediaType?.lowercase()) {
                                    "tv" -> "TV"
                                    "movie" -> "Movie"
                                    else -> "Unknown"
                                }
                                val genre = mediaItem.genreIds.take(1).map { getGenreName(it) }.first()
                                "$type • $genre"
                            } else {
                                // For other screens, show two genres
                                mediaItem.genreIds.take(2).map { getGenreName(it) }.joinToString(" • ")
                            }
                        } else {
                            // For screens without genres, show only type
                            when (mediaItem.mediaType?.lowercase()) {
                                "tv" -> "TV"
                                "movie" -> "Movie"
                                else -> "Unknown"
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = String.format("%.1f", mediaItem.voteAverage ?: 0.0),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = mediaItem.overview?.takeIf { it.isNotBlank() } ?: "Overview not available",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    if (showModal) {
        MediaDetailModal(
            mediaItem = mediaItem,
            onDismiss = { showModal = false },
            sharedViewModel = sharedViewModel,
            authViewModel = authViewModel,
            isFavoritesList = isFavoritesList
        )
    }
}

private fun getGenreName(genreId: Int): String {
    return when (genreId) {
        28 -> "Action"
        12 -> "Adventure"
        16 -> "Animation"
        35 -> "Comedy"
        80 -> "Crime"
        99 -> "Documentary"
        18 -> "Drama"
        10751 -> "Family"
        14 -> "Fantasy"
        36 -> "History"
        27 -> "Horror"
        10402 -> "Music"
        9648 -> "Mystery"
        10749 -> "Romance"
        878 -> "Science Fiction"
        10770 -> "TV Movie"
        53 -> "Thriller"
        10752 -> "War"
        37 -> "Western"
        else -> "Unknown Genre"
    }
}
