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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.model.MediaItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun MediaItemList(
    mediaItems: List<MediaItem>,
    headerTitle: String = "",
    showGenre: Boolean = false,
    onLoadMore: () -> Unit = {},
    showHeader: Boolean = true,
    showLoadingIndicator: Boolean = false
) {
    val listState = rememberLazyListState()

    // Trigger load more when reaching near the end
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
        modifier = Modifier,
        state = listState
    ) {
        if (showHeader) {
            item {
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        items(mediaItems) { mediaItem ->
            MediaItemCard(
                mediaItem = mediaItem,
                showGenre = showGenre
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
