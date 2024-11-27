package com.example.wdww.components

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
import com.example.wdww.model.MediaItem
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel
import android.content.res.Configuration

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullscreenMediaPager(
    mediaItems: List<MediaItem>,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
    val pagerState = rememberPagerState(pageCount = { mediaItems.size })
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val mediaItem = mediaItems[page]
            
            AsyncImage(
                model = if (isPortrait) {
                    "https://image.tmdb.org/t/p/original${mediaItem.posterPath}"
                } else {
                    "https://image.tmdb.org/t/p/original${mediaItem.backdropPath ?: mediaItem.posterPath}"
                },
                contentDescription = mediaItem.title ?: mediaItem.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clickable { selectedMedia = mediaItem },
                contentScale = if (isPortrait) ContentScale.Fit else ContentScale.Crop
            )
        }
    }

    // Show modal when a media item is selected
    selectedMedia?.let { media ->
        MediaDetailModal(
            mediaItem = media,
            onDismiss = { selectedMedia = null },
            sharedViewModel = sharedViewModel,
            authViewModel = authViewModel
        )
    }
}
