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
import java.time.LocalDate
import com.example.wdww.viewmodel.AuthViewModel

@Composable
fun TheatersScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf("Coming Soon", "Now Playing")

    Column(modifier = Modifier.fillMaxSize()) {
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
                val response = RetrofitInstance.api.getUpcomingMovies(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    page = 1
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { upcomingResponse ->
                        val today = LocalDate.now()
                        val upcomingMovies = upcomingResponse.results.filter { movie ->
                            movie.releaseDate?.let { releaseDate ->
                                LocalDate.parse(releaseDate).isAfter(today)
                            } ?: false
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
            MediaItemList(
                mediaItems = allMediaItems,
                headerTitle = "Coming Soon",
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = RetrofitInstance.api.getUpcomingMovies(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    page = currentPage
                                )
                                
                                if (response.isSuccessful) {
                                    response.body()?.let { upcomingResponse ->
                                        val today = LocalDate.now()
                                        val upcomingMovies = upcomingResponse.results.filter { movie ->
                                            movie.releaseDate?.let { releaseDate ->
                                                LocalDate.parse(releaseDate).isAfter(today)
                                            } ?: false
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
                val response = RetrofitInstance.api.getNowPlayingMovies(
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
            MediaItemList(
                mediaItems = allMediaItems,
                headerTitle = "Now Playing",
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = RetrofitInstance.api.getNowPlayingMovies(
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
