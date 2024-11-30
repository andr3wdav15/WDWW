/**
 * Fullscreen Media Pager Component.
 * This component implements a horizontal pager that displays media items (movies/TV shows) 
 * in a fullscreen view with support for both portrait and landscape orientations.
 */
package com.example.wdww.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wdww.model.media.MediaItem
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel
import android.content.res.Configuration

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
    
    // Initialize pager state with the number of media items
    val pagerState = rememberPagerState(pageCount = { mediaItems.size })
    
    // Get current device configuration to determine orientation
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // Container for the pager
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Horizontal pager implementation
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val mediaItem = mediaItems[page]
            
            // Display media image using Coil's AsyncImage
            AsyncImage(
                // Choose appropriate image path based on orientation
                model = if (isPortrait) {
                    "https://image.tmdb.org/t/p/original${mediaItem.posterPath}"
                } else {
                    // Fallback to poster path if backdrop is not available
                    "https://image.tmdb.org/t/p/original${mediaItem.backdropPath ?: mediaItem.posterPath}"
                },
                contentDescription = mediaItem.title ?: mediaItem.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clickable { selectedMedia = mediaItem },  // Show detail modal on click
                // Adjust content scale based on orientation
                contentScale = if (isPortrait) ContentScale.Fit else ContentScale.Crop
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
