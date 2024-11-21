package com.example.wdww.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.MediaItemList
import com.example.wdww.model.MediaItem
import com.example.wdww.network.RetrofitInstance
import com.example.wdww.viewmodel.SharedViewModel
import kotlinx.coroutines.launch

@Composable
fun MoviesScreen(sharedViewModel: SharedViewModel) {
    val pagerState = rememberPagerState { 6 }  // Number of pages
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        "All", 
        "Netflix", 
        "Disney+",
        "Apple TV",
        "Crave",
        "Paramount+"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp  // Remove extra padding at edges
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

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> PopularMoviesContent(sharedViewModel)
                1 -> StreamingServiceMoviesContent(sharedViewModel, "Netflix", "8")
                2 -> StreamingServiceMoviesContent(sharedViewModel, "Disney+", "337")
                3 -> StreamingServiceMoviesContent(sharedViewModel, "Apple TV", "350")
                4 -> StreamingServiceMoviesContent(sharedViewModel, "Crave", "230")
                5 -> StreamingServiceMoviesContent(sharedViewModel, "Paramount+", "531")
            }
        }
    }
}

@Composable
private fun PopularMoviesContent(sharedViewModel: SharedViewModel) {
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
                val response = RetrofitInstance.api.getPopularMovies(
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
            MediaItemList(
                mediaItems = allMediaItems,
                headerTitle = "Popular",
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = RetrofitInstance.api.getPopularMovies(
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
                }
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

@Composable
private fun StreamingServiceMoviesContent(
    sharedViewModel: SharedViewModel,
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
                val response = RetrofitInstance.api.getMoviesByProvider(
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
            MediaItemList(
                mediaItems = allMediaItems,
                headerTitle = "$serviceName",
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = RetrofitInstance.api.getMoviesByProvider(
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
                }
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
