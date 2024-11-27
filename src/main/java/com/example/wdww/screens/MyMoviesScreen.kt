package com.example.wdww.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.FullscreenMediaPager
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

@Composable
fun MyMoviesScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val favoriteMovies by sharedViewModel.favoriteMovies.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val accountId by authViewModel.accountId.collectAsState()
    
    LaunchedEffect(accountId) {
        accountId?.let { id ->
            authViewModel.getSessionId()?.let { sessionId ->
                sharedViewModel.refreshFavoriteMovies(id, sessionId)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (favoriteMovies.isNotEmpty()) {
            FullscreenMediaPager(
                mediaItems = favoriteMovies,
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        } else if (error == null) {
            Text(
                text = "No favorite movies found",
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