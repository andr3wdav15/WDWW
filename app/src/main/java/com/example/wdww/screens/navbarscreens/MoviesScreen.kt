/**
 * Movies screen implementation for WDWW app using Jetpack Compose.
 * Provides a tabbed interface to browse movies from different streaming services.
 * Features include:
 * - Popular movies across all platforms
 * - Service-specific movie listings (Netflix, Disney+, etc.)
 * - Responsive layout for both portrait and landscape orientations
 * - Infinite scrolling with pagination
 * - Error handling and loading states
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
import com.example.wdww.components.MediaList
import com.example.wdww.model.media.MediaItem
import com.example.wdww.network.NetworkClient
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

/**
 * Main Movies screen composable that provides a tabbed interface for browsing movies.
 * Adapts its layout based on device orientation and manages navigation between different
 * streaming service content.
 *
 * @param sharedViewModel ViewModel for sharing data between screens
 * @param authViewModel ViewModel for handling authentication state
 */
@Composable
fun MoviesScreen(sharedViewModel: SharedViewModel, authViewModel: AuthViewModel) {
    val pagerState = rememberPagerState { 6 }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    
    val pages = listOf(
        "All",
        "Netflix",
        "Disney+",
        "Apple TV",
        "Crave",
        "Paramount+"
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
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp
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
                0 -> PopularMoviesContent(sharedViewModel, authViewModel)
                1 -> StreamingServiceMoviesContent(sharedViewModel, authViewModel, "Netflix", "8")
                2 -> StreamingServiceMoviesContent(sharedViewModel, authViewModel, "Disney+", "337")
                3 -> StreamingServiceMoviesContent(sharedViewModel, authViewModel, "Apple TV", "350")
                4 -> StreamingServiceMoviesContent(sharedViewModel, authViewModel, "Crave", "230")
                5 -> StreamingServiceMoviesContent(sharedViewModel, authViewModel, "Paramount+", "531")
            }
        }
    }
}

/**
 * Composable that displays a list of popular movies across all platforms.
 * Implements infinite scrolling with pagination and handles loading/error states.
 *
 * Features:
 * - Loads popular movies from TMDB API
 * - Implements infinite scrolling with pagination
 * - Displays loading indicators during data fetch
 * - Handles and displays error states
 *
 * @param sharedViewModel ViewModel for sharing data between screens
 * @param authViewModel ViewModel for handling authentication state
 */
@Composable
private fun PopularMoviesContent(
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
                val response = NetworkClient.api.getFilteredPopularMovies(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    page = 1
                )

                if (response.isSuccessful) {
                    response.body()?.let { moviesResponse ->
                        allMediaItems.addAll(moviesResponse.results)
                        hasMorePages = currentPage < moviesResponse.totalPages
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
                headerTitle = "Popular",
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = NetworkClient.api.getFilteredPopularMovies(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    page = currentPage
                                )

                                if (response.isSuccessful) {
                                    response.body()?.let { moviesResponse ->
                                        allMediaItems.addAll(moviesResponse.results)
                                        hasMorePages = currentPage < moviesResponse.totalPages
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

        if (isLoading && allMediaItems.isEmpty()) {
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
 * Composable that displays movies from a specific streaming service.
 * Implements infinite scrolling with pagination and handles loading/error states.
 *
 * Features:
 * - Loads movies specific to a streaming provider
 * - Implements infinite scrolling with pagination
 * - Displays loading indicators during data fetch
 * - Handles and displays error states
 *
 * @param sharedViewModel ViewModel for sharing data between screens
 * @param authViewModel ViewModel for handling authentication state
 * @param serviceName Name of the streaming service to display (e.g., "Netflix", "Disney+")
 * @param providerId TMDB provider ID for the streaming service
 */
@Composable
private fun StreamingServiceMoviesContent(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel,
    serviceName: String,
    providerId: String
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
                val response = NetworkClient.api.getMoviesByProvider(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    watchProvider = providerId,
                    watchRegion = "CA",
                    page = currentPage
                )

                if (response.isSuccessful) {
                    response.body()?.let { moviesResponse ->
                        allMediaItems.addAll(moviesResponse.results)
                        hasMorePages = currentPage < moviesResponse.totalPages
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
                headerTitle = serviceName,
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = NetworkClient.api.getMoviesByProvider(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    watchProvider = providerId,
                                    watchRegion = "CA",
                                    page = currentPage
                                )

                                if (response.isSuccessful) {
                                    response.body()?.let { moviesResponse ->
                                        allMediaItems.addAll(moviesResponse.results)
                                        hasMorePages = currentPage < moviesResponse.totalPages
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

        if (isLoading && allMediaItems.isEmpty()) {
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