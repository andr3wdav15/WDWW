package com.example.wdww.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.wdww.model.Cast
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailModal(
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel,
    isFavoritesList: Boolean = false
) {
    val mediaDetails by sharedViewModel.selectedMediaDetails.collectAsState()
    val accountId by authViewModel.accountId.collectAsState()
    val alerts by sharedViewModel.alerts.collectAsState()
    val favoriteMovies by sharedViewModel.favoriteMovies.collectAsState()
    val favoriteTVShows by sharedViewModel.favoriteTVShows.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Check if the media is in favorites - if we're in favorites list, we know it's true
    // otherwise check the lists
    val isFavorite = remember(mediaItem, favoriteMovies, favoriteTVShows, isFavoritesList) {
        if (isFavoritesList) true
        else {
            when (mediaItem.mediaType) {
                "movie" -> favoriteMovies.any { it.id == mediaItem.id }
                "tv" -> favoriteTVShows.any { it.id == mediaItem.id }
                else -> false
            }
        }
    }
    
    // Check if the media is an upcoming release
    val isUpcoming = remember(mediaItem) {
        val releaseDate = mediaItem.releaseDate?.let { 
            LocalDate.parse(it)
        } ?: mediaItem.firstAirDate?.let {
            LocalDate.parse(it)
        }
        
        releaseDate?.isAfter(LocalDate.now()) ?: false
    }

    LaunchedEffect(mediaItem.id) {
        sharedViewModel.fetchMediaDetails(mediaItem.id, mediaItem.mediaType ?: "movie")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Backdrop Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                mediaDetails?.backdropPath?.let { backdropPath ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://image.tmdb.org/t/p/original$backdropPath")
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title and Action Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mediaDetails?.title ?: mediaDetails?.name ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (isUpcoming) {
                        IconButton(
                            onClick = {
                                if (sharedViewModel.isAlertSet(mediaItem.id)) {
                                    sharedViewModel.removeAlert(context, mediaItem.id)
                                } else {
                                    sharedViewModel.addAlert(
                                        context = context,
                                        movieId = mediaItem.id,
                                        movieTitle = mediaItem.title ?: mediaItem.name ?: "",
                                        releaseDate = mediaItem.releaseDate ?: mediaItem.firstAirDate ?: "",
                                        posterPath = mediaItem.posterPath,
                                        mediaType = mediaItem.mediaType ?: "movie"
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (sharedViewModel.isAlertSet(mediaItem.id)) {
                                    Icons.Filled.Notifications
                                } else {
                                    Icons.Outlined.Notifications
                                },
                                contentDescription = if (sharedViewModel.isAlertSet(mediaItem.id)) {
                                    "Remove Alert"
                                } else {
                                    "Set Alert"
                                },
                                tint = if (sharedViewModel.isAlertSet(mediaItem.id)) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    } else {
                        // Show heart icon for non-upcoming releases
                        IconButton(
                            onClick = {
                                scope.launch {
                                    accountId?.let { id ->
                                        authViewModel.getSessionId()?.let { sessionId ->
                                            if (isFavorite) {
                                                sharedViewModel.removeFromFavorites(
                                                    mediaId = mediaItem.id,
                                                    mediaType = mediaItem.mediaType ?: "movie",
                                                    accountId = id,
                                                    sessionId = sessionId
                                                )
                                            } else {
                                                sharedViewModel.addToFavorites(
                                                    mediaId = mediaItem.id,
                                                    mediaType = mediaItem.mediaType ?: "movie",
                                                    accountId = id,
                                                    sessionId = sessionId
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Release Date (if available)
                mediaItem.releaseDate?.let { releaseDate ->
                    Text(
                        text = "Release Date: $releaseDate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Director
                mediaDetails?.credits?.crew?.find { it.job == "Director" }?.let { director ->
                    Text(
                        text = "Directed by ${director.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Genres
                Text(
                    text = mediaDetails?.genres?.joinToString(", ") { it.name } ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Overview
                Text(
                    text = mediaDetails?.overview ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cast Section
                if (!mediaDetails?.credits?.cast.isNullOrEmpty()) {
                    Text(
                        text = "Cast",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        mediaDetails?.credits?.cast?.take(10)?.forEach { cast ->
                            item {
                                CastCard(cast)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CastCard(cast: Cast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    if (!cast.profilePath.isNullOrEmpty()) {
                        "https://image.tmdb.org/t/p/w185${cast.profilePath}"
                    } else {
                        "https://ui-avatars.com/api/?name=${cast.name}&size=185"
                    }
                )
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = cast.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}