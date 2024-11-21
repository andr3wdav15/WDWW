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
import com.example.wdww.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun MyTVScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    var accountId by remember { mutableStateOf<Int?>(null) }

    // Get account details first
    LaunchedEffect(Unit) {
        try {
            val sessionId = authViewModel.getSessionId()
            if (sessionId != null) {
                val accountResponse = RetrofitInstance.api.getAccountDetails(
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    sessionId = sessionId
                )
                if (accountResponse.isSuccessful) {
                    accountId = accountResponse.body()?.id
                }
            }
        } catch (e: Exception) {
            error = "Error fetching account details: ${e.localizedMessage}"
        }
    }

    // Load favorite TV shows
    LaunchedEffect(accountId) {
        if (accountId != null && allMediaItems.isEmpty()) {
            try {
                val sessionId = authViewModel.getSessionId()
                if (sessionId != null) {
                    val response = RetrofitInstance.api.getFavoriteTVShows(
                        accountId = accountId!!,
                        apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                        sessionId = sessionId
                    )
                    
                    if (response.isSuccessful) {
                        response.body()?.let { tvShowsResponse ->
                            allMediaItems.addAll(tvShowsResponse.results)
                        }
                    } else {
                        error = "Error: ${response.code()} - ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (allMediaItems.isNotEmpty()) {
            MediaItemList(
                mediaItems = allMediaItems,
                headerTitle = "My TV Shows",
                showGenre = true,
                onLoadMore = { /* No pagination for favorites */ },
                showLoadingIndicator = false
            )
        } else if (error == null) {
            Text(
                text = "No favorite TV shows found",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
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