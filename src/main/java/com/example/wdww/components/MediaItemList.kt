package com.example.wdww.components

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
import com.example.wdww.model.MediaItem
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

@Composable
fun MediaItemList(
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
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val horizontalPadding = if (!isPortrait) 64.dp else 16.dp

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
        .distinctUntilChanged()
        .map { lastIndex ->
            lastIndex != null && lastIndex >= mediaItems.size - 2
        }
        .collect { shouldLoadMore ->
            if (shouldLoadMore) {
                onLoadMore()
            }
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            horizontal = horizontalPadding,
            vertical = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (showHeader && headerTitle.isNotEmpty()) {
            item {
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }
        }

        items(mediaItems) { mediaItem ->
            MediaItemCard(
                mediaItem = mediaItem,
                showGenre = showGenre,
                showTypeWithGenre = showTypeWithGenre,
                isFavoritesList = isFavoritesList,
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }

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
