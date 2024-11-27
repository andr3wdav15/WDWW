package com.example.wdww.components

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalConfiguration
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
import android.util.Log

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
    val favoriteMovies by sharedViewModel.favoriteMovies.collectAsState()
    val favoriteTVShows by sharedViewModel.favoriteTVShows.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    // Check if the media is an upcoming release
    val isUpcoming = remember(mediaItem) {
        val releaseDate = mediaItem.releaseDate?.let { 
            LocalDate.parse(it)
        } ?: mediaItem.firstAirDate?.let {
            LocalDate.parse(it)
        }

        releaseDate?.isAfter(LocalDate.now()) ?: false
    }

    // Get favorite status - check both movie and TV show lists
    val isFavorite by remember(mediaItem.id, favoriteMovies, favoriteTVShows) {
        derivedStateOf {
            when {
                // If we're in the favorites list, it's definitely a favorite
                isFavoritesList -> true
                // Check if it's in the TV shows list
                mediaItem.mediaType == "tv" -> favoriteTVShows.any { it.id == mediaItem.id }
                // Otherwise check both lists
                else -> favoriteMovies.any { it.id == mediaItem.id } || 
                        favoriteTVShows.any { it.id == mediaItem.id }
            }
        }
    }

    // Handle favorite toggle
    val handleFavoriteToggle: () -> Unit = {
        Log.d("MediaDetailModal", "Toggling favorite status for ID: ${mediaItem.id}")
        accountId?.let { id ->
            scope.launch {
                authViewModel.getSessionId()?.let { sessionId ->
                    // Determine media type
                    val mediaType = when {
                        // If we're in favorites list and in MyTVShows screen, it's definitely a TV show
                        isFavoritesList && mediaItem.mediaType == "tv" -> "tv"
                        // If mediaType is explicitly set to tv, use it
                        mediaItem.mediaType == "tv" -> "tv"
                        // If it has a name but no title, it's likely a TV show
                        mediaItem.title == null && mediaItem.name != null -> "tv"
                        // If we're in favorites list and it has a name, assume it's a TV show
                        isFavoritesList && mediaItem.name != null -> "tv"
                        // Default to movie
                        else -> "movie"
                    }
                    Log.d("MediaDetailModal", "Using type: $mediaType for favorite toggle")

                    if (isFavorite) {
                        // Remove from favorites and wait for UI update
                        sharedViewModel.removeFromFavorites(
                            mediaId = mediaItem.id,
                            mediaType = mediaType,
                            accountId = id,
                            sessionId = sessionId
                        )
                        // If we're in favorites list and removing a favorite, dismiss the modal
                        if (isFavoritesList) {
                            onDismiss()
                        }
                    } else {
                        sharedViewModel.addToFavorites(
                            mediaId = mediaItem.id,
                            mediaType = mediaType,
                            accountId = id,
                            sessionId = sessionId
                        )
                    }

                }
            }
        }
    }

    LaunchedEffect(mediaItem.id, mediaItem.mediaType) {
        Log.d("MediaDetailModal", "Opening modal for: ${mediaItem.title ?: mediaItem.name} (ID: ${mediaItem.id}, Type: ${mediaItem.mediaType})")
        
        // Determine the media type
        val type = when {
            // If we're in favorites list and it has a name (TV show title), it's a TV show
            isFavoritesList && mediaItem.name != null -> "tv"
            // If mediaType is explicitly set, use it
            !mediaItem.mediaType.isNullOrEmpty() -> mediaItem.mediaType
            // If it has a name but no title, it's likely a TV show
            mediaItem.title == null && mediaItem.name != null -> "tv"
            // Default to movie
            else -> "movie"
        }
        
        Log.d("MediaDetailModal", "Using type: $type for API call with ID: ${mediaItem.id}")
        sharedViewModel.fetchMediaDetails(mediaItem.id, type)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = isLandscape),
        modifier = if (isLandscape) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
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
                        val alerts by sharedViewModel.alerts.collectAsState()
                        val isAlertSet = alerts.any { it.mediaId == mediaItem.id }
                        
                        IconButton(
                            onClick = {
                                if (isAlertSet) {
                                    scope.launch {
                                        sharedViewModel.removeAlert(context, mediaItem.id)
                                        // Also remove from theatre list if we have a session
                                        authViewModel.getSessionId()?.let { sessionId ->
                                            sharedViewModel.removeFromTheatreList(mediaItem.id, sessionId)
                                        }
                                    }
                                } else {
                                    scope.launch {
                                        val sessionId = authViewModel.getSessionId()
                                        sharedViewModel.addAlert(
                                            context = context,
                                            movieId = mediaItem.id,
                                            movieTitle = mediaItem.title ?: mediaItem.name ?: "",
                                            releaseDate = mediaItem.releaseDate ?: mediaItem.firstAirDate ?: "",
                                            posterPath = mediaItem.posterPath,
                                            mediaType = mediaItem.mediaType ?: "movie",
                                            sessionId = sessionId
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isAlertSet) {
                                    Icons.Filled.Notifications
                                } else {
                                    Icons.Outlined.Notifications
                                },
                                contentDescription = if (isAlertSet) {
                                    "Remove Alert"
                                } else {
                                    "Set Alert"
                                },
                                tint = if (isAlertSet) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    } else {
                        // Show heart icon for non-upcoming releases
                        IconButton(
                            onClick = handleFavoriteToggle
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
                    text = mediaDetails?.overview?.takeIf { it.isNotBlank() } ?: "Overview not available",
                    style = MaterialTheme.typography.bodyLarge,
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