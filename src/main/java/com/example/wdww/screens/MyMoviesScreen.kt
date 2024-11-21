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

@Composable
fun MyMoviesScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val allMediaItems = remember { mutableStateListOf<MediaItem>() }
    var error by remember { mutableStateOf<String?>(null) }
    var accountId by remember { mutableStateOf<Int?>(null) }

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

    LaunchedEffect(accountId) {
        if (accountId != null && allMediaItems.isEmpty()) {
            try {
                val sessionId = authViewModel.getSessionId()
                if (sessionId != null) {
                    val response = RetrofitInstance.api.getFavoriteMovies(
                        accountId = accountId!!,
                        apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                        sessionId = sessionId
                    )
                    
                    if (response.isSuccessful) {
                        response.body()?.let { moviesResponse ->
                            allMediaItems.addAll(moviesResponse.results)
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
                headerTitle = "My Movies",
                showGenre = true,
                onLoadMore = { /* No pagination for favorites */ },
                showLoadingIndicator = false,
                authViewModel = authViewModel,
                sharedViewModel = sharedViewModel
            )
        } else if (error == null) {
            Text(
                text = "No favorite movies found",
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