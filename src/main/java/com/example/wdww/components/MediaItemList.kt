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
import com.example.wdww.model.MediaItem
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

@Composable
fun MediaItemList(
    mediaItems: List<MediaItem>,
    headerTitle: String = "",
    showGenre: Boolean = false,
    onLoadMore: () -> Unit = {},
    showHeader: Boolean = true,
    showLoadingIndicator: Boolean = false,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val listState = rememberLazyListState()

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
            top = if (showHeader) 16.dp else 0.dp,
            bottom = 16.dp
        )
    ) {
        if (showHeader) {
            item {
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        items(mediaItems) { mediaItem ->
            MediaItemCard(
                mediaItem = mediaItem,
                showGenre = showGenre,
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
