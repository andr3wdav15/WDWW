/**
 * Search screen implementation for WDWW.
 * Provides real-time search functionality for movies and TV shows.
 * 
 * Features:
 * - Real-time search with debouncing (300ms delay)
 * - Infinite scrolling with pagination
 * - Filters results to only movies and TV shows
 * - Loading states and error handling
 * - Keyboard management
 * - Back navigation with search query clearing
 */
package com.example.wdww.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.MediaList
import com.example.wdww.model.media.MediaItem
import com.example.wdww.network.NetworkClient
import com.example.wdww.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.wdww.viewmodel.AuthViewModel

/**
 * Main search screen composable that handles media search functionality.
 * 
 * This screen:
 * - Performs real-time search as the user types (with debouncing)
 * - Displays search results in a scrollable list
 * - Implements infinite scrolling for pagination
 * - Manages loading states and error handling
 * - Provides back navigation with proper keyboard and state cleanup
 *
 * Search behavior:
 * - Triggers search when query is 2 or more characters
 * - Debounces search by 300ms to prevent API spam
 * - Filters results to only show movies and TV shows
 * - Clears results when query is empty
 *
 * @param sharedViewModel ViewModel for sharing media data and search state
 * @param navController Navigation controller for handling screen navigation
 * @param authViewModel ViewModel for handling authentication state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    var hasMorePages by remember { mutableStateOf(true) }
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val searchQuery by sharedViewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty() && searchQuery.length >= 2) {
            delay(300)
            try {
                isLoading = true
                currentPage = 1
                allMediaItems.clear()
                
                val response = NetworkClient.api.searchMedia(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    query = searchQuery,
                    page = currentPage
                )

                if (response.isSuccessful) {
                    response.body()?.let { searchResponse ->
                        val filteredResults = searchResponse.results.filter { item ->
                            item.mediaType == "movie" || item.mediaType == "tv"
                        }
                        allMediaItems.addAll(filteredResults)
                        hasMorePages = currentPage < searchResponse.totalPages
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
        } else {
            allMediaItems.clear()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    sharedViewModel.updateSearchQuery("")
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Search Results",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            MediaList(
                mediaItems = allMediaItems,
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = NetworkClient.api.searchMedia(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    query = searchQuery,
                                    page = currentPage
                                )

                                if (response.isSuccessful) {
                                    response.body()?.let { searchResponse ->
                                        val filteredResults = searchResponse.results.filter { item ->
                                            item.mediaType == "movie" || item.mediaType == "tv"
                                        }
                                        allMediaItems.addAll(filteredResults)
                                        hasMorePages = currentPage < searchResponse.totalPages
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
                showHeader = false,
                showLoadingIndicator = isLoading,
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )

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
} 