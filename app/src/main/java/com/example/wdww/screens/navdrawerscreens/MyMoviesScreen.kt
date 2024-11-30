/**
 * My Movies screen implementation for WDWW.
 * Displays the user's favorite movies in a fullscreen pager format.
 * 
 * Features:
 * - Automatic loading of favorite movies when account ID is available
 * - Fullscreen swipeable movie presentation
 * - Handles empty states and error conditions
 * - Real-time updates when favorites change
 */
package com.example.wdww.screens.navdrawerscreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.ui.components.MediaPager
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

/**
 * Main composable for the My Movies screen that displays the user's favorite movies.
 * 
 * This screen:
 * - Automatically fetches favorite movies when the user's account ID becomes available
 * - Displays movies in a fullscreen swipeable format using [MediaPager]
 * - Shows appropriate messages for empty states
 * - Displays error messages when something goes wrong
 *
 * The screen observes:
 * - Favorite movies from [SharedViewModel]
 * - Error states from [SharedViewModel]
 * - Account ID from [AuthViewModel]
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state and user data
 */
@Composable
fun MyMoviesScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val favoriteMovies by sharedViewModel.favoriteMovies.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val accountId by authViewModel.accountId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(accountId) {
        accountId?.let { id ->
            authViewModel.getSessionId()?.let { sessionId ->
                sharedViewModel.refreshFavoriteMovies(id, sessionId)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (favoriteMovies.isNotEmpty()) {
                MediaPager(
                    mediaItems = favoriteMovies,
                    sharedViewModel = sharedViewModel,
                    authViewModel = authViewModel
                )
            } else if (error == null) {
                Text(
                    text = "No movies found",
                    style = MaterialTheme.typography.bodyLarge,
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
} 