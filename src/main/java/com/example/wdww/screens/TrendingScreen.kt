package com.example.wdww.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.example.wdww.components.MediaItemList
import com.example.wdww.model.MediaItem
import com.example.wdww.model.TrendingResponse
import com.example.wdww.network.RetrofitInstance
import com.example.wdww.viewmodel.SharedViewModel
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingScreen(
    sharedViewModel: SharedViewModel
) {
    var isLoading by remember { mutableStateOf(true) }
    val allMediaItems = remember { mutableStateListOf<MediaItem>()
    }


    LaunchedEffect(Unit) {
        val response: Response<TrendingResponse> = try {
            RetrofitInstance.api.getTrending("c5479e7394cd551bad2a1af7e9bff8a3")
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            return@LaunchedEffect
        }

        if (response.isSuccessful) {
            val trendingResponse = response.body()
            trendingResponse?.results?.let { itemList ->
                allMediaItems.addAll(itemList)
            }
        } else {
            println("API Error: ${response.errorBody()?.string()}")
        }
        isLoading = false
    }

    val searchQuery by sharedViewModel.searchQuery.collectAsState()

    val filteredMediaItems = if (searchQuery.isEmpty()) {
        allMediaItems
    } else {
        allMediaItems.filter { mediaItem ->
            val title = mediaItem.title ?: mediaItem.name ?: ""
            title.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        content = { innerPadding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                MediaItemList(
                    mediaItems = filteredMediaItems,
                    modifier = Modifier.padding(innerPadding),
                    headerTitle = "Trending"
                )
            }
        }
    )
}
