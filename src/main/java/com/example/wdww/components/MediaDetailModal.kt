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
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailModal(
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val mediaDetails by sharedViewModel.selectedMediaDetails.collectAsState()
    
    LaunchedEffect(mediaItem.id) {
        sharedViewModel.fetchMediaDetails(mediaItem.id, mediaItem.mediaType ?: "movie")
    }

    // Add this for debugging
    LaunchedEffect(mediaDetails) {
        println("Debug - Backdrop Path: ${mediaDetails?.backdropPath}")
        println("Debug - Cast: ${mediaDetails?.credits?.cast?.take(3)?.map { it.profilePath }}")
        println("Debug - Cast Details:")
        println("Total cast members: ${mediaDetails?.credits?.cast?.size}")
        mediaDetails?.credits?.cast?.take(3)?.forEach { cast ->
            println("Cast member: ${cast.name}")
            println("Profile path: ${cast.profilePath}")
            println("Full URL: https://image.tmdb.org/t/p/w185${cast.profilePath}")
        }
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
                // Title
                Text(
                    text = mediaDetails?.title ?: mediaDetails?.name ?: "",
                    style = MaterialTheme.typography.headlineSmall
                )

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