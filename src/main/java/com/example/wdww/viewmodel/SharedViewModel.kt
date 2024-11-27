package com.example.wdww.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wdww.network.AddToFavoritesRequest
import com.example.wdww.network.StatusResponse
import com.example.wdww.model.MediaDetails
import com.example.wdww.model.MediaItem
import com.example.wdww.model.Alert
import com.example.wdww.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wdww.receivers.ReleaseNotificationReceiver
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class SharedViewModel : ViewModel() {
    private val TAG = "SharedViewModel"
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedMediaDetails = MutableStateFlow<MediaDetails?>(null)
    val selectedMediaDetails: StateFlow<MediaDetails?> = _selectedMediaDetails.asStateFlow()

    private val _favoriteMovies = MutableStateFlow<List<MediaItem>>(emptyList())
    val favoriteMovies: StateFlow<List<MediaItem>> = _favoriteMovies.asStateFlow()

    private val _favoriteTVShows = MutableStateFlow<List<MediaItem>>(emptyList())
    val favoriteTVShows: StateFlow<List<MediaItem>> = _favoriteTVShows.asStateFlow()

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()

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
                        apiKey = API_KEY
                    )
                    "tv" -> RetrofitInstance.api.getTVShowDetails(
                        tvId = mediaId,
                        apiKey = API_KEY
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
                    apiKey = API_KEY,
                    sessionId = sessionId,
                    body = AddToFavoritesRequest(
                        mediaType = mediaType,
                        mediaId = mediaId,
                        favorite = true
                    )
                )

                if (response.isSuccessful) {
                    // Refresh the appropriate list
                    if (mediaType == "movie") {
                        refreshFavoriteMovies(accountId, sessionId)
                    } else {
                        refreshFavoriteTVShows(accountId, sessionId)
                    }
                } else {
                    _error.value = "Error adding to favorites: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error adding to favorites: ${e.localizedMessage}"
            }
        }
    }

    fun removeFromFavorites(mediaId: Int, mediaType: String, accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Removing from favorites: $mediaId ($mediaType)")
                val response = RetrofitInstance.api.addToFavorites(
                    accountId = accountId,
                    apiKey = API_KEY,
                    sessionId = sessionId,
                    body = AddToFavoritesRequest(
                        mediaType = mediaType,
                        mediaId = mediaId,
                        favorite = false
                    )
                )

                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully removed from favorites")
                    // Refresh the appropriate list
                    if (mediaType == "movie") {
                        refreshFavoriteMovies(accountId, sessionId)
                    } else {
                        refreshFavoriteTVShows(accountId, sessionId)
                    }
                } else {
                    _error.value = "Error removing from favorites: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing from favorites", e)
                _error.value = "Error removing from favorites: ${e.localizedMessage}"
            }
        }
    }

    fun refreshFavoriteMovies(accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getFavoriteMovies(
                    accountId = accountId,
                    apiKey = API_KEY,
                    sessionId = sessionId
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { moviesResponse ->
                        _favoriteMovies.value = moviesResponse.results
                    }
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun refreshFavoriteTVShows(accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getFavoriteTVShows(
                    accountId = accountId,
                    apiKey = API_KEY,
                    sessionId = sessionId
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { tvShowsResponse ->
                        _favoriteTVShows.value = tvShowsResponse.results
                    }
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun addAlert(
        context: Context,
        movieId: Int,
        movieTitle: String,
        releaseDate: String,
        posterPath: String?,
        mediaType: String
    ) {
        try {
            Log.d(TAG, "Adding alert for movie: $movieId - $movieTitle")
            
            // Create the alert object first
            val alert = Alert(
                mediaId = movieId,
                title = movieTitle,
                releaseDate = releaseDate,
                posterPath = posterPath,
                mediaType = mediaType
            )

            // Add to alerts list
            val currentAlerts = _alerts.value
            _alerts.value = currentAlerts + alert
            Log.d(TAG, "Current alerts size: ${_alerts.value.size}")

            // Schedule the notification
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReleaseNotificationReceiver::class.java).apply {
                putExtra("movieId", movieId)
                putExtra("movieTitle", movieTitle)
                putExtra("releaseDate", releaseDate)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                movieId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Parse the release date and set notification for the day before
            val date = LocalDate.parse(releaseDate)
            val notificationDate = date.minusDays(1)
            val triggerTime = notificationDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            // Schedule the notification
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            
            Log.d(TAG, "Successfully added alert and scheduled notification")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add alert", e)
            _error.value = "Failed to add alert: ${e.localizedMessage}"
        }
    }

    fun removeAlert(context: Context, movieId: Int) {
        try {
            Log.d(TAG, "Removing alert for movie: $movieId")
            
            // Cancel the notification
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReleaseNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                movieId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)

            // Remove from alerts list
            val currentAlerts = _alerts.value
            _alerts.value = currentAlerts.filterNot { it.mediaId == movieId }
            Log.d(TAG, "Current alerts size after removal: ${_alerts.value.size}")
            
            Log.d(TAG, "Successfully removed alert and cancelled notification")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove alert", e)
            _error.value = "Failed to remove alert: ${e.localizedMessage}"
        }
    }

    fun isAlertSet(movieId: Int): Boolean {
        val isSet = _alerts.value.any { it.mediaId == movieId }
        Log.d(TAG, "Checking if alert is set for movie $movieId: $isSet")
        return isSet
    }

    companion object {
        private const val API_KEY = "c5479e7394cd551bad2a1af7e9bff8a3"
    }
}