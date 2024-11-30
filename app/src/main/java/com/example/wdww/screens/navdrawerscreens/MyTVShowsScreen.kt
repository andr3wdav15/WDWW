/**
 * My TV Shows screen implementation for WDWW.
 * Displays the user's favorite TV shows in a fullscreen pager format.
 * 
 * Features:
 * - Automatic loading of favorite TV shows when account ID is available
 * - Fullscreen swipeable TV show presentation
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
 * Main composable for the My TV Shows screen that displays the user's favorite TV shows.
 * 
 * This screen:
 * - Automatically fetches favorite TV shows when the user's account ID becomes available
 * - Displays TV shows in a fullscreen swipeable format using [MediaPager]
 * - Shows appropriate messages for empty states
 * - Displays error messages when something goes wrong
 *
 * The screen observes:
 * - Favorite TV shows from [SharedViewModel]
 * - Error states from [SharedViewModel]
 * - Account ID from [AuthViewModel]
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state and user data
 */
@Composable
fun MyTVShowsScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val favoriteTVShows by sharedViewModel.favoriteTVShows.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val accountId by authViewModel.accountId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(accountId) {
        accountId?.let { id ->
            authViewModel.getSessionId()?.let { sessionId ->
                sharedViewModel.refreshFavoriteTVShows(id, sessionId)
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
            if (favoriteTVShows.isNotEmpty()) {
                MediaPager(
                    mediaItems = favoriteTVShows,
                    sharedViewModel = sharedViewModel,
                    authViewModel = authViewModel,
                    isFromNavDrawer = true
                )
            } else if (error == null) {
                Text(
                    text = "No TV shows found",
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
