package com.example.wdww.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.MediaItemList
import com.example.wdww.model.MediaItem
import com.example.wdww.network.RetrofitInstance
import com.example.wdww.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    var isLoading by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    var hasMorePages by remember { mutableStateOf(true) }
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val searchQuery by sharedViewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Debounced search
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {  // Only search if 2 or more characters
            delay(300)  // Wait 300ms after last keystroke
            try {
                isLoading = true
                currentPage = 1
                allMediaItems.clear()
                
                val response = RetrofitInstance.api.searchMedia(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    query = searchQuery,
                    page = currentPage
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { searchResponse ->
                        allMediaItems.addAll(searchResponse.results)
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
            // Clear results if search query is too short
            allMediaItems.clear()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Search Results") },
            navigationIcon = {
                IconButton(
                    onClick = { 
                        keyboardController?.hide()
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            MediaItemList(
                mediaItems = allMediaItems,
                showGenre = true,
                onLoadMore = {
                    if (!isLoading && hasMorePages) {
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                val response = RetrofitInstance.api.searchMedia(
                                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                                    query = searchQuery,
                                    page = currentPage
                                )
                                
                                if (response.isSuccessful) {
                                    response.body()?.let { searchResponse ->
                                        allMediaItems.addAll(searchResponse.results)
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
                showLoadingIndicator = isLoading
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