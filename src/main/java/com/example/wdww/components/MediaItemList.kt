package com.example.wdww.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.model.MediaItem

@Composable
fun MediaItemList(
    mediaItems: List<MediaItem>,
    modifier: Modifier = Modifier,
    headerTitle: String? = null
) {
    LazyColumn(modifier = modifier) {
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
    }
}
