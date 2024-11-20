package com.example.wdww.screens

import androidx.compose.foundation.layout.*
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
fun TrendingScreen(sharedViewModel: SharedViewModel) {
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(1) }
    var hasMorePages by remember { mutableStateOf(true) }
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load initial data
    LaunchedEffect(Unit) {
        if (allMediaItems.isEmpty()) {
            try {
                isLoading = true
                val response = RetrofitInstance.api.getTrending(
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
            MediaItemList(
                mediaItems = allMediaItems,
                headerTitle = "Trending",
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = RetrofitInstance.api.getTrending(
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
                }
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
