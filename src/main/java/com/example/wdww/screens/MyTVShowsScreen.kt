package com.example.wdww.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.MediaItemList
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

@Composable
fun MyTVShowsScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val favoriteTVShows by sharedViewModel.favoriteTVShows.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val accountId by authViewModel.accountId.collectAsState()
    
    LaunchedEffect(accountId) {
        accountId?.let { id ->
            authViewModel.getSessionId()?.let { sessionId ->
                sharedViewModel.refreshFavoriteTVShows(id, sessionId)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (favoriteTVShows.isNotEmpty()) {
            MediaItemList(
                mediaItems = favoriteTVShows,
                headerTitle = "My TV Shows",
                showGenre = true,
                onLoadMore = { /* No pagination for favorites */ },
                showLoadingIndicator = false,
                isFavoritesList = true,
                authViewModel = authViewModel,
                sharedViewModel = sharedViewModel
            )
        } else if (error == null) {
            Text(
                text = "No favorite TV shows found",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }

        error?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}
