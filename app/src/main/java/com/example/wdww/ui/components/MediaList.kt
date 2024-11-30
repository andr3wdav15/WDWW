/**
 * Media Item List Component.
 * This component implements a scrollable list of media items with support for infinite scrolling,
 * header display, and responsive layout adjustments based on screen orientation.
 * It also handles loading states and provides a consistent layout for displaying media items.
 */
package com.example.wdww.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.ui.text.font.FontWeight
import com.example.wdww.model.media.MediaItem
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

/**
 * A composable function that creates a scrollable list of media items with various display options.
 * Supports infinite scrolling, header display, and adapts layout based on screen orientation.
 *
 * @param mediaItems List of media items to display
 * @param headerTitle Optional title to display at the top of the list
 * @param showGenre Whether to display genre information in media items
 * @param showTypeWithGenre Whether to show media type along with genre
 * @param onLoadMore Callback function triggered when more items should be loaded
 * @param showHeader Whether to display the header section
 * @param showLoadingIndicator Whether to show a loading indicator at the bottom
 * @param isFavoritesList Whether this list is displaying favorite items
 * @param sharedViewModel ViewModel for sharing media-related data
 * @param authViewModel ViewModel for handling authentication state
 */
@Composable
fun MediaList(
    mediaItems: List<MediaItem>,
    headerTitle: String = "",
    showGenre: Boolean = false,
    showTypeWithGenre: Boolean = false,
    onLoadMore: () -> Unit = {},
    showHeader: Boolean = true,
    showLoadingIndicator: Boolean = false,
    isFavoritesList: Boolean = false,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    // Initialize list state for scroll position tracking
    val listState = rememberLazyListState()
    
    // Get current device configuration for responsive layout
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val horizontalPadding = if (!isPortrait) 64.dp else 16.dp

    // Set up infinite scrolling behavior
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
        .distinctUntilChanged()
        .map { lastIndex ->
            // Trigger load more when reaching near the end of the list
            lastIndex != null && lastIndex >= mediaItems.size - 2
        }
        .collect { shouldLoadMore ->
            if (shouldLoadMore) {
                onLoadMore()
            }
        }
    }

    // Main list container
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            horizontal = horizontalPadding,
            vertical = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header section with title and divider
        if (showHeader && headerTitle.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
                ) {
                    // Header title
                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Decorative divider under title
                    Divider(
                        modifier = Modifier.width(40.dp),
                        thickness = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // List of media items
        items(mediaItems) { mediaItem ->
            MediaCard(
                mediaItem = mediaItem,
                showGenre = showGenre,
                showTypeWithGenre = showTypeWithGenre,
                isFavoritesList = isFavoritesList,
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }

        // Loading indicator at the bottom of the list
        if (showLoadingIndicator && mediaItems.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
