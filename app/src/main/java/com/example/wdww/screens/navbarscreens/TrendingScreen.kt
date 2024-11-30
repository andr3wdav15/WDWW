/**
 * Trending screen implementation for WDWW.
 * Provides a tabbed interface to browse trending media content by genre.
 * 
 * Features:
 * - Overall trending content across all genres
 * - Genre-specific trending content (Action, Comedy, Drama, etc.)
 * - Responsive layout for both portrait and landscape orientations
 * - Infinite scrolling with pagination
 * - Loading states and error handling
 * - Mixed media types (movies and TV shows) support
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

/**
 * Main trending screen composable that provides a tabbed interface for browsing trending content.
 * Adapts its layout based on device orientation and manages navigation between different genres.
 *
 * The screen provides tabs for:
 * - All trending content
 * - Genre-specific content (Action, Comedy, Drama, Horror, Romance, Sci-Fi)
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val pagerState = rememberPagerState { 7 }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    
    val pages = listOf(
        "All",
        "Action",
        "Comedy",
        "Drama",
        "Horror",
        "Romance",
        "Sci-Fi"
    )

    val genreIds = mapOf(
        "Action" to 28,
        "Comedy" to 35,
        "Drama" to 18,
        "Horror" to 27,
        "Romance" to 10749,
        "Sci-Fi" to 878
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
                0 -> AllTrendingContent(sharedViewModel, authViewModel)
                else -> GenreTrendingContent(
                    sharedViewModel = sharedViewModel,
                    authViewModel = authViewModel,
                    genreName = pages[page],
                    genreId = genreIds[pages[page]] ?: 0
                )
            }
        }
    }
}

/**
 * Composable that displays all trending content across genres.
 * Implements infinite scrolling with pagination and handles loading/error states.
 *
 * Features:
 * - Displays mixed media types (movies and TV shows)
 * - Shows genre and media type information
 * - Implements infinite scrolling
 * - Handles loading and error states
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state
 */
@Composable
private fun AllTrendingContent(
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
                val response = NetworkClient.api.getTrending(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    page = 1
                )

                if (response.isSuccessful) {
                    response.body()?.let { trendingResponse ->
                        allMediaItems.addAll(trendingResponse.results)
                        hasMorePages = currentPage < trendingResponse.totalPages
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
                headerTitle = "Trending Now",
                showGenre = true,
                showTypeWithGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = NetworkClient.api.getTrending(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    page = currentPage
                                )

                                if (response.isSuccessful) {
                                    response.body()?.let { trendingResponse ->
                                        allMediaItems.addAll(trendingResponse.results)
                                        hasMorePages = currentPage < trendingResponse.totalPages
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
 * Composable that displays trending content for a specific genre.
 * Implements infinite scrolling with pagination and handles loading/error states.
 *
 * Features:
 * - Filters content by specific genre
 * - Shows genre and media type information
 * - Implements infinite scrolling
 * - Handles loading and error states
 *
 * @param sharedViewModel ViewModel for sharing media data between screens
 * @param authViewModel ViewModel for handling authentication state
 * @param genreName Display name of the genre (e.g., "Action", "Comedy")
 * @param genreId TMDB genre ID for filtering content
 */
@Composable
private fun GenreTrendingContent(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel,
    genreName: String,
    genreId: Int
) {
    var isLoading by remember { mutableStateOf(false) }
    var moviePage by remember { mutableIntStateOf(1) }
    var tvPage by remember { mutableIntStateOf(1) }
    var hasMoreMovies by remember { mutableStateOf(true) }
    var hasMoreTVShows by remember { mutableStateOf(true) }
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (allMediaItems.isEmpty()) {
            try {
                isLoading = true

                val movieResponse = NetworkClient.api.discoverMovies(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    genreId = genreId.toString(),
                    page = 1
                )

                val tvResponse = NetworkClient.api.discoverTVShows(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    genreId = genreId.toString(),
                    page = 1
                )

                if (movieResponse.isSuccessful && tvResponse.isSuccessful) {
                    movieResponse.body()?.let { response ->
                        response.results.forEach { item ->
                            allMediaItems.add(item.copy(mediaType = "movie"))
                        }
                        hasMoreMovies = 1 < response.totalPages
                        if (hasMoreMovies) moviePage++
                    }

                    tvResponse.body()?.let { response ->
                        response.results.forEach { item ->
                            allMediaItems.add(item.copy(mediaType = "tv"))
                        }
                        hasMoreTVShows = 1 < response.totalPages
                        if (hasMoreTVShows) tvPage++
                    }

                    val sortedList = allMediaItems.sortedByDescending { it.voteAverage }
                    allMediaItems.clear()
                    allMediaItems.addAll(sortedList)

                } else {
                    error = "Error loading content"
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
                headerTitle = genreName,
                showGenre = true,
                showTypeWithGenre = true,
                onLoadMore = {
                    if (!isLoading && (hasMoreMovies || hasMoreTVShows)) {
                        coroutineScope.launch {
                            try {
                                isLoading = true

                                if (hasMoreMovies) {
                                    val movieResponse = NetworkClient.api.discoverMovies(
                                        apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                        genreId = genreId.toString(),
                                        page = moviePage
                                    )

                                    if (movieResponse.isSuccessful) {
                                        movieResponse.body()?.let { response ->
                                            response.results.forEach { item ->
                                                allMediaItems.add(item.copy(mediaType = "movie"))
                                            }
                                            hasMoreMovies = moviePage < response.totalPages
                                            if (hasMoreMovies) moviePage++
                                        }
                                    }
                                }

                                if (hasMoreTVShows) {
                                    val tvResponse = NetworkClient.api.discoverTVShows(
                                        apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                        genreId = genreId.toString(),
                                        page = tvPage
                                    )
                                    
                                    if (tvResponse.isSuccessful) {
                                        tvResponse.body()?.let { response ->
                                            response.results.forEach { item ->
                                                allMediaItems.add(item.copy(mediaType = "tv"))
                                            }
                                            hasMoreTVShows = tvPage < response.totalPages
                                            if (hasMoreTVShows) tvPage++
                                        }
                                    }
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
