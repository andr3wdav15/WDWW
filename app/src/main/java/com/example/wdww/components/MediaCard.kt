/**
 * Media Item Card Component.
 * This component displays a card view for a media item (movie or TV show) with its poster,
 * title, genre, rating, and overview. It supports different display modes and handles
 * user interaction to show detailed information in a modal.
 */
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
import com.example.wdww.model.media.MediaItem
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel

/**
 * A composable function that creates a card view for displaying media item information.
 * The card shows the media item's poster, title, genre, rating, and a brief overview.
 * Clicking the card opens a detailed modal view of the media item.
 *
 * @param mediaItem The media item (movie/TV show) to display
 * @param showGenre Whether to display the genre information
 * @param showTypeWithGenre Whether to show media type (Movie/TV) along with genre
 * @param isFavoritesList Whether this card is being displayed in a favorites list
 * @param sharedViewModel ViewModel for sharing media-related data
 * @param authViewModel ViewModel for handling authentication state
 */
@SuppressLint("DefaultLocale")
@Composable
fun MediaCard(
    mediaItem: MediaItem,
    showGenre: Boolean = false,
    showTypeWithGenre: Boolean = false,
    isFavoritesList: Boolean = false,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    // State for controlling the detail modal visibility
    var showModal by remember { mutableStateOf(false) }
    rememberCoroutineScope()

    // Main card container
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                Log.d("MediaCard", "Card clicked: ${mediaItem.name ?: mediaItem.title} (ID: ${mediaItem.id}, Type: ${mediaItem.mediaType})")
                showModal = true
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RectangleShape
    ) {
        // Horizontal layout for poster and content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Media poster image
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

            // Content column (title, genre, rating, overview)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                // Title row
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

                // Genre and rating row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Genre or media type text
                    Text(
                        text = if (showGenre && !mediaItem.genreIds.isNullOrEmpty()) {
                            if (showTypeWithGenre) {
                                val type = when (mediaItem.mediaType?.lowercase()) {
                                    "tv" -> "TV"
                                    "movie" -> "Movie"
                                    else -> "Unknown"
                                }
                                val genre = mediaItem.genreIds.take(1).map { getGenreName(it) }.first()
                                "$type • $genre"
                            } else {
                                mediaItem.genreIds.take(2).map { getGenreName(it) }.joinToString(" • ")
                            }
                        } else {
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

                    // Rating display
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

                // Overview text
                Text(
                    text = mediaItem.overview?.takeIf { it.isNotBlank() } ?: "Overview not available",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    // Detail modal
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

/**
 * Helper function to convert genre IDs to their corresponding names.
 * Maps standard TMDB genre IDs to human-readable genre names.
 *
 * @param genreId The TMDB genre ID
 * @return The corresponding genre name, or "Unknown Genre" if not found
 */
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
