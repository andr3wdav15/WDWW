/**
 * Theaters screen implementation for WDWW.
 * Provides a tabbed interface to browse movies in theaters and upcoming releases.
 * 
 * Features:
 * - Two main sections: "Coming Soon" and "Now Playing"
 * - Date-based filtering for upcoming movies
 * - Responsive layout for both portrait and landscape orientations
 * - Infinite scrolling with pagination
 * - Loading states and error handling
 */
package com.example.wdww.screens.navbarscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import com.example.wdww.ui.components.MediaList
import com.example.wdww.model.media.MediaItem
import com.example.wdww.network.NetworkClient
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Main theaters screen composable that provides a tabbed interface for browsing
 * movies in theaters and upcoming releases.
 *
 * The screen provides two main tabs:
 * - Coming Soon: Shows upcoming movie releases
 * - Now Playing: Shows movies currently in theaters
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheatersScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val pagerState = rememberPagerState { 2 }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    
    val pages = listOf(
        "Coming Soon",
        "Now Playing"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = if (!isPortrait) 64.dp else 0.dp,
                end = if (!isPortrait) 64.dp else 0.dp,
                top = if (!isPortrait) 24.dp else 0.dp
            )
    ) {
        if (isPortrait) {
            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        } else {
            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> UpcomingMoviesContent(sharedViewModel, authViewModel)
                1 -> NowPlayingContent(sharedViewModel, authViewModel)
            }
        }
    }
}

/**
 * Composable that displays upcoming movie releases.
 * Implements infinite scrolling with pagination and handles loading/error states.
 *
 * Features:
 * - Filters movies to only show future releases
 * - Uses LocalDate for date-based filtering
 * - Shows movie genres
 * - Implements infinite scrolling
 * - Handles loading and error states
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state
 */
@Composable
private fun UpcomingMoviesContent(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    var hasMorePages by remember { mutableStateOf(true) }
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load initial data
    LaunchedEffect(Unit) {
        if (allMediaItems.isEmpty()) {
            try {
                isLoading = true
                val response = NetworkClient.api.getUpcomingMovies(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    page = 1
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { upcomingResponse ->
                        val today = LocalDate.now()
                        val upcomingMovies = upcomingResponse.results.filter { movie ->
                            movie.releaseDate?.let { releaseDate ->
                                LocalDate.parse(releaseDate).isAfter(today)
                            } == true
                        }
                        allMediaItems.addAll(upcomingMovies)
                        hasMorePages = currentPage < upcomingResponse.totalPages
                        currentPage++
                    }
                } else {
                    error = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (allMediaItems.isNotEmpty()) {
            MediaList(
                mediaItems = allMediaItems,
                headerTitle = "Coming Soon",
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = NetworkClient.api.getUpcomingMovies(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    page = currentPage
                                )
                                
                                if (response.isSuccessful) {
                                    response.body()?.let { upcomingResponse ->
                                        val today = LocalDate.now()
                                        val upcomingMovies = upcomingResponse.results.filter { movie ->
                                            movie.releaseDate?.let { releaseDate ->
                                                LocalDate.parse(releaseDate).isAfter(today)
                                            } == true
                                        }
                                        allMediaItems.addAll(upcomingMovies)
                                        hasMorePages = currentPage < upcomingResponse.totalPages
                                        currentPage++
                                    }
                                } else {
                                    error = "Error: ${response.code()} - ${response.message()}"
                                }
                            } catch (e: Exception) {
                                error = "Error: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                showLoadingIndicator = isLoading,
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
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

/**
 * Composable that displays movies currently playing in theaters.
 * Implements infinite scrolling with pagination and handles loading/error states.
 *
 * Features:
 * - Shows currently playing movies
 * - Shows movie genres
 * - Implements infinite scrolling
 * - Handles loading and error states
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state
 */
@Composable
private fun NowPlayingContent(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    var hasMorePages by remember { mutableStateOf(true) }
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (allMediaItems.isEmpty()) {
            try {
                isLoading = true
                val response = NetworkClient.api.getNowPlayingMovies(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    page = 1
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { nowPlayingResponse ->
                        allMediaItems.addAll(nowPlayingResponse.results)
                        hasMorePages = currentPage < nowPlayingResponse.totalPages
                        currentPage++
                    }
                } else {
                    error = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (allMediaItems.isNotEmpty()) {
            MediaList(
                mediaItems = allMediaItems,
                headerTitle = "Now Playing",
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = NetworkClient.api.getNowPlayingMovies(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    page = currentPage
                                )
                                
                                if (response.isSuccessful) {
                                    response.body()?.let { nowPlayingResponse ->
                                        allMediaItems.addAll(nowPlayingResponse.results)
                                        hasMorePages = currentPage < nowPlayingResponse.totalPages
                                        currentPage++
                                    }
                                } else {
                                    error = "Error: ${response.code()} - ${response.message()}"
                                }
                            } catch (e: Exception) {
                                error = "Error: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                showLoadingIndicator = isLoading,
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
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
