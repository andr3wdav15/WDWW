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
    modifier: Modifier = Modifier,
    headerTitle: String? = null,
    onLoadMore: () -> Unit
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
        modifier = modifier,
        state = listState
    ) {
        if (!headerTitle.isNullOrEmpty()) {
            item {
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 12.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        
        items(mediaItems) { mediaItem ->
            MediaItemCard(mediaItem)
        }

        if (mediaItems.isNotEmpty()) {
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
