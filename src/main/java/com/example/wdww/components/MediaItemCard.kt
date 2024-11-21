package com.example.wdww.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.rememberAsyncImagePainter
import com.example.wdww.model.MediaItem

@Composable
fun MediaItemCard(mediaItem: MediaItem, showGenre: Boolean = false) {
    val title = mediaItem.title ?: mediaItem.name ?: "Unknown Title"
    val releaseDate = mediaItem.releaseDate ?: mediaItem.firstAirDate ?: "Unknown Date"
    
    val displayType = if (showGenre) {
        mediaItem.genreIds?.firstOrNull()?.let { genreId ->
            getGenreName(genreId)
        } ?: "Unknown Genre"
    } else {
        when (mediaItem.mediaType?.lowercase()) {
            "tv" -> "TV"
            else -> mediaItem.mediaType?.replaceFirstChar { it.uppercase() } ?: "Unknown Type"
        }
    }
    
    val posterUrl = "https://image.tmdb.org/t/p/w500${mediaItem.posterPath}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(posterUrl),
            contentDescription = title,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Release Date: $releaseDate",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = displayType,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = mediaItem.overview ?: "No description available.",
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        }
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
