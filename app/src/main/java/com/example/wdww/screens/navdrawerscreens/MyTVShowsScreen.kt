/**
 * My TV Shows screen implementation for WDWW.
 * Displays the user's favorite TV shows in a fullscreen pager format.
 * 
 * Features:
 * - Automatic loading of favorite TV shows when account ID is available
 * - Fullscreen swipeable TV show presentation
 * - Handles empty states and error conditions
 * - Real-time updates when favorites change
 * - Debug logging for tracking TV show data and state changes
 */
package com.example.wdww.screens.navdrawerscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.MediaPager
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel
import android.util.Log

/**
 * Main composable for the My TV Shows screen that displays the user's favorite TV shows.
 * 
 * This screen:
 * - Automatically fetches favorite TV shows when the user's account ID becomes available
 * - Displays TV shows in a fullscreen swipeable format using [MediaPager]
 * - Shows appropriate messages for empty states
 * - Displays error messages when something goes wrong
 * - Includes debug logging for tracking data flow and state changes
 *
 * The screen observes:
 * - Favorite TV shows from [SharedViewModel]
 * - Error states from [SharedViewModel]
 * - Account ID from [AuthViewModel]
 *
 * Debug logs include:
 * - When favorite TV shows are being refreshed
 * - Number of TV shows being displayed
 * - Details of each TV show (name, ID, media type)
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
    
    LaunchedEffect(accountId) {
        accountId?.let { id ->
            authViewModel.getSessionId()?.let { sessionId ->
                Log.d("MyTVShowsScreen", "Refreshing favorite TV shows for account: $id")
                sharedViewModel.refreshFavoriteTVShows(id, sessionId)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (favoriteTVShows.isNotEmpty()) {
            Log.d("MyTVShowsScreen", "Displaying ${favoriteTVShows.size} favorite TV shows")
            favoriteTVShows.forEach { show ->
                Log.d("MyTVShowsScreen", "TV Show: ${show.name ?: show.title} (ID: ${show.id}, Type: ${show.mediaType})")
            }
            MediaPager(
                mediaItems = favoriteTVShows,
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        } else if (error == null) {
            Text(
                text = "No TV shows found",
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
