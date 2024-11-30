/**
 * Fullscreen Media Pager Component.
 * This component implements a horizontal pager that displays media items (movies/TV shows) 
 * in a fullscreen view with support for both portrait and landscape orientations.
 */
package com.example.wdww.ui.components

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wdww.model.media.MediaItem
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

/**
 * A composable function that creates a fullscreen horizontal pager for displaying media items.
 * Supports both portrait and landscape orientations, automatically adjusting the image display accordingly.
 * When a media item is clicked, it shows a detailed modal view of the item.
 *
 * @param mediaItems List of media items (movies/TV shows) to display in the pager
 * @param sharedViewModel ViewModel for sharing data between components
 * @param authViewModel ViewModel for handling authentication state
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaPager(
    mediaItems: List<MediaItem>,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    // State to track the currently selected media item for showing the detail modal
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
    
    // Initialize pager state with the number of media items and save it across configuration changes
    val currentPage by sharedViewModel.currentPagerPage.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { mediaItems.size }
    )
    rememberCoroutineScope()
    
    // Save the current page when it changes
    LaunchedEffect(pagerState.currentPage) {
        sharedViewModel.setCurrentPagerPage(pagerState.currentPage)
        val currentItem = mediaItems[pagerState.currentPage]
        val type = when {
            currentItem.mediaType == "movie" || currentItem.releaseDate != null -> "movie"
            currentItem.mediaType == "tv" || currentItem.firstAirDate != null -> "tv"
            else -> "movie"
        }
        sharedViewModel.fetchMediaDetails(currentItem.id, type)
    }

    // Get current device configuration to determine orientation
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val context = LocalContext.current
    val activity = context as Activity
    val window = activity.window
    
    // Handle system bars visibility based on orientation
    DisposableEffect(isPortrait) {
        if (!isPortrait) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, window.decorView).apply {
                show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            // Restore system bars when component is disposed
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, window.decorView).apply {
                show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Container for the pager
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Horizontal pager implementation
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val mediaItem = mediaItems[page]
            val mediaDetails by sharedViewModel.selectedMediaDetails.collectAsState()
            
            // Debug logging
            Log.d("MediaPager", "Orientation: ${if (isPortrait) "Portrait" else "Landscape"}")
            Log.d("MediaPager", "MediaDetails BackdropPath: ${mediaDetails?.backdropPath}")
            Log.d("MediaPager", "PosterPath: ${mediaItem.posterPath}")
            Log.d("MediaPager", "BackdropPath: ${mediaItem.backdropPath}")
            
            // Display media image using Coil's AsyncImage
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (isPortrait) {
                            "https://image.tmdb.org/t/p/original${mediaItem.posterPath}"
                        } else {
                            mediaDetails?.backdropPath?.let { backdropPath ->
                                "https://image.tmdb.org/t/p/original$backdropPath"
                            } ?: "https://image.tmdb.org/t/p/original${mediaItem.posterPath}"
                        }
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = mediaItem.title ?: mediaItem.name,
                modifier = Modifier
                    .fillMaxSize()
                    .let { modifier ->
                        if (!isPortrait) {
                            // Remove padding in landscape mode for edge-to-edge display
                            modifier
                        } else {
                            modifier.padding(8.dp)
                        }
                    }
                    .clickable { selectedMedia = mediaItem },  
                contentScale = if (isPortrait) ContentScale.Fit else ContentScale.FillWidth
            )
        }
    }

    // Show detail modal if a media item is selected
    selectedMedia?.let { media ->
        MediaDetailModal(
            mediaItem = media,
            onDismiss = { selectedMedia = null },
            sharedViewModel = sharedViewModel,
            authViewModel = authViewModel
        )
    }
}
