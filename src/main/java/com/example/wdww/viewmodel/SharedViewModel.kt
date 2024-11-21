package com.example.wdww.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wdww.model.MediaItem
import com.example.wdww.network.AddToFavoritesRequest
import com.example.wdww.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedMediaDetails = MutableStateFlow<MediaDetails?>(null)
    val selectedMediaDetails = _selectedMediaDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun fetchMediaDetails(mediaId: Int, mediaType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = when (mediaType) {
                    "movie" -> RetrofitInstance.api.getMovieDetails(
                        movieId = mediaId,
                        apiKey = "c5479e7394cd551bad2a1af7e9bff8a3"
                    )
                    "tv" -> RetrofitInstance.api.getTVShowDetails(
                        tvId = mediaId,
                        apiKey = "c5479e7394cd551bad2a1af7e9bff8a3"
                    )
                    else -> throw IllegalArgumentException("Invalid media type")
                }

                if (response.isSuccessful) {
                    _selectedMediaDetails.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorites(mediaId: Int, mediaType: String, accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addToFavorites(
                    accountId = accountId,
                    apiKey = "c5479e7394cd551bad2a1af7e9bff8a3",
                    sessionId = sessionId,
                    body = AddToFavoritesRequest(
                        mediaType = mediaType,
                        mediaId = mediaId,
                        favorite = true
                    )
                )

                if (!response.isSuccessful) {
                    _error.value = "Error adding to favorites: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error adding to favorites: ${e.localizedMessage}"
            }
        }
    }

    fun clearSelectedMediaDetails() {
        _selectedMediaDetails.value = null
        _error.value = null
    }

    fun scheduleMovieAlert(mediaItem: MediaItem) {
        viewModelScope.launch {
            try {
                // TODO: Implement notification scheduling
                // We'll need to use WorkManager for this
                // Will implement in next step
            } catch (e: Exception) {
                _error.value = "Error scheduling alert: ${e.localizedMessage}"
            }
        }
    }
}