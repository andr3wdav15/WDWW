/**
 * My Alerts screen implementation for WDWW.
 * Displays user's release alerts for upcoming movies and TV shows.
 * 
 * Features:
 * - Automatic loading of theatre list when authenticated
 * - Fullscreen swipeable presentation of alerts
 * - Release date-based sorting
 * - Comprehensive error handling with Snackbar notifications
 * - Loading states
 */
package com.example.wdww.screens.navdrawerscreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.ui.components.MediaPager
import com.example.wdww.model.media.MediaItem
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * Main alerts screen composable that displays the user's release alerts.
 * 
 * This screen:
 * - Automatically fetches the user's theatre list when mounted
 * - Displays alerts in a fullscreen swipeable format using [MediaPager]
 * - Shows loading indicators during data fetch
 * - Displays error messages using Snackbar
 * - Handles authentication state
 *
 * The screen observes:
 * - Alerts from [SharedViewModel]
 * - Error states from [SharedViewModel]
 * - Account ID from [AuthViewModel]
 *
 * @param sharedViewModel ViewModel for sharing media data and alert state
 * @param authViewModel ViewModel for handling authentication state and user data
 */
@Composable
fun MyAlertsScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val alerts by sharedViewModel.alerts.collectAsState()
    val error by sharedViewModel.error.collectAsState()
    val accountId by authViewModel.accountId.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val sessionId = authViewModel.getSessionId()
            if (sessionId != null && accountId != null) {
                val success = sharedViewModel.getTheatreList(accountId!!, sessionId)
                if (!success) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Failed to get theatre list")
                    }
                }
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Please log in to view alerts")
                }
            }
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Error: ${e.localizedMessage}")
            }
        } finally {
            isLoading = false
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (alerts.isNotEmpty()) {
                val mediaItems = alerts.sortedBy { it.releaseDate }.map { alert ->
                    MediaItem(
                        id = alert.mediaId,
                        title = alert.title,
                        name = null,
                        overview = null,
                        posterPath = alert.posterPath,
                        backdropPath = alert.posterPath,
                        releaseDate = alert.releaseDate,
                        firstAirDate = null,
                        mediaType = alert.mediaType,
                        voteAverage = null,
                        genreIds = null
                    )
                }
                
                MediaPager(
                    mediaItems = mediaItems,
                    sharedViewModel = sharedViewModel,
                    authViewModel = authViewModel,
                    isFromNavDrawer = true
                )
            } else {
                Text(
                    text = "No alerts found",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            error?.let { errorMessage ->
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }
    }
}