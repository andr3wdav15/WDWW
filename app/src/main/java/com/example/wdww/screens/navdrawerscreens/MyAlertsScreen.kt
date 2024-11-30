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
 * - Debug logging for tracking data flow and state changes
 * - Authentication state management
 */
package com.example.wdww.screens.navdrawerscreens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.MediaPager
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
 * - Includes comprehensive debug logging
 *
 * The screen observes:
 * - Alerts from [SharedViewModel]
 * - Error states from [SharedViewModel]
 * - Account ID from [AuthViewModel]
 *
 * Debug logs include:
 * - Alert list updates and counts
 * - Individual alert details
 * - Authentication state changes
 * - Error conditions
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

    LaunchedEffect(alerts) {
        Log.d("MyAlertsScreen", "Alerts updated: ${alerts.size} items")
        alerts.forEach { alert ->
            Log.d("MyAlertsScreen", "Alert: ${alert.title} (${alert.mediaId})")
        }
    }

    LaunchedEffect(Unit) {
        Log.d("MyAlertsScreen", "LaunchedEffect triggered")
        isLoading = true
        try {
            val sessionId = authViewModel.getSessionId()
            Log.d("MyAlertsScreen", "SessionId: $sessionId, AccountId: $accountId")
            
            if (sessionId != null && accountId != null) {
                Log.d("MyAlertsScreen", "Calling getTheatreList")
                val success = sharedViewModel.getTheatreList(accountId!!, sessionId)
                Log.d("MyAlertsScreen", "getTheatreList result: $success")
                
                if (!success) {
                    Log.e("MyAlertsScreen", "Failed to get theatre list")
                    scope.launch {
                        snackbarHostState.showSnackbar("Failed to get theatre list")
                    }
                }
            } else {
                Log.e("MyAlertsScreen", "Missing sessionId or accountId")
                scope.launch {
                    snackbarHostState.showSnackbar("Please log in to view alerts")
                }
            }
        } catch (e: Exception) {
            Log.e("MyAlertsScreen", "Error loading theatre list", e)
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
                Log.d("MyAlertsScreen", "Displaying ${alerts.size} alerts")
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
                    authViewModel = authViewModel
                )
            } else {
                Log.d("MyAlertsScreen", "No alerts found")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No alerts found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            error?.let { errorMessage ->
                Log.e("MyAlertsScreen", "Showing error: $errorMessage")
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }
    }
}