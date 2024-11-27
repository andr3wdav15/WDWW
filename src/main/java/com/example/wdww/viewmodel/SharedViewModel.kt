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
    val searchQuery: StateFlow<String> = _searchQuery

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedMediaDetails = MutableStateFlow<MediaDetails?>(null)
    val selectedMediaDetails: StateFlow<MediaDetails?> = _selectedMediaDetails

    private val _favoriteMovies = MutableStateFlow<List<MediaItem>>(emptyList())
    val favoriteMovies: StateFlow<List<MediaItem>> = _favoriteMovies

    private val _favoriteTVShows = MutableStateFlow<List<MediaItem>>(emptyList())
    val favoriteTVShows: StateFlow<List<MediaItem>> = _favoriteTVShows

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun fetchMediaDetails(mediaId: Int, mediaType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d(TAG, "Fetching details for $mediaType with ID: $mediaId")
                val response = when (mediaType) {
                    "movie" -> RetrofitInstance.api.getMovieDetails(
                        movieId = mediaId,
                        apiKey = API_KEY
                    )
                    "tv" -> RetrofitInstance.api.getTVShowDetails(
                        tvId = mediaId,
                        apiKey = API_KEY
                    )
                    else -> throw IllegalArgumentException("Invalid media type: $mediaType")
                }

                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully fetched details for $mediaType ID: $mediaId")
                    _selectedMediaDetails.value = response.body()
                } else {
                    Log.e(TAG, "Error fetching details: ${response.code()} - ${response.message()}")
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching details", e)
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorites(mediaId: Int, mediaType: String, accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Adding to favorites: ID=$mediaId, Type=$mediaType")

                // Find existing item from current media details
                val existingItem = when (mediaType) {
                    "tv" -> _favoriteTVShows.value.find { it.id == mediaId }
                    "movie" -> _favoriteMovies.value.find { it.id == mediaId }
                    else -> null
                }

                // Use selected media details if available
                val mediaDetails = _selectedMediaDetails.value
                
                // Create item with all available information
                val newItem = existingItem ?: MediaItem(
                    id = mediaId,
                    title = mediaDetails?.title,
                    name = mediaDetails?.name,
                    overview = mediaDetails?.overview,
                    posterPath = mediaDetails?.backdropPath,
                    releaseDate = null,
                    firstAirDate = null,
                    mediaType = mediaType,
                    voteAverage = null,
                    genreIds = mediaDetails?.genres?.map { it.id }
                )

                // Optimistically update UI
                when (mediaType) {
                    "tv" -> {
                        if (!_favoriteTVShows.value.any { it.id == mediaId }) {
                            _favoriteTVShows.value = _favoriteTVShows.value + newItem
                        }
                    }
                    "movie" -> {
                        if (!_favoriteMovies.value.any { it.id == mediaId }) {
                            _favoriteMovies.value = _favoriteMovies.value + newItem
                        }
                    }
                }

                // Make API call in background
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
                            // Refresh in background to get complete details
                            when (mediaType) {
                                "tv" -> refreshFavoriteTVShows(accountId, sessionId)
                                "movie" -> refreshFavoriteMovies(accountId, sessionId)
                            }
                        } else {
                            Log.e(TAG, "Error adding to favorites: ${response.code()} - ${response.message()}")
                            _error.value = "Failed to add to favorites"
                            // Revert the optimistic update
                            when (mediaType) {
                                "tv" -> {
                                    _favoriteTVShows.value = _favoriteTVShows.value.filter { it.id != mediaId }
                                    refreshFavoriteTVShows(accountId, sessionId)
                                }
                                "movie" -> {
                                    _favoriteMovies.value = _favoriteMovies.value.filter { it.id != mediaId }
                                    refreshFavoriteMovies(accountId, sessionId)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception adding to favorites", e)
                        _error.value = "Error: ${e.localizedMessage}"
                        // Revert the optimistic update
                        when (mediaType) {
                            "tv" -> {
                                _favoriteTVShows.value = _favoriteTVShows.value.filter { it.id != mediaId }
                                refreshFavoriteTVShows(accountId, sessionId)
                            }
                            "movie" -> {
                                _favoriteMovies.value = _favoriteMovies.value.filter { it.id != mediaId }
                                refreshFavoriteMovies(accountId, sessionId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in addToFavorites", e)
                _error.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun removeFromFavorites(mediaId: Int, mediaType: String, accountId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Removing from favorites: ID=$mediaId, Type=$mediaType")

                // Optimistically update UI immediately
                when (mediaType) {
                    "tv" -> {
                        _favoriteTVShows.emit(_favoriteTVShows.value.filter { it.id != mediaId })
                        Log.d(TAG, "Updated TV Shows list, new size: ${_favoriteTVShows.value.size}")
                    }
                    "movie" -> {
                        _favoriteMovies.emit(_favoriteMovies.value.filter { it.id != mediaId })
                        Log.d(TAG, "Updated Movies list, new size: ${_favoriteMovies.value.size}")
                    }
                }

                // Make API call
                try {
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
                    
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Error removing from favorites: ${response.code()} - ${response.message()}")
                        _error.emit("Failed to remove from favorites")
                        // Revert the optimistic update
                        when (mediaType) {
                            "tv" -> refreshFavoriteTVShows(accountId, sessionId)
                            "movie" -> refreshFavoriteMovies(accountId, sessionId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception removing from favorites", e)
                    _error.emit("Error: ${e.localizedMessage}")
                    // Revert the optimistic update
                    when (mediaType) {
                        "tv" -> refreshFavoriteTVShows(accountId, sessionId)
                        "movie" -> refreshFavoriteMovies(accountId, sessionId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in removeFromFavorites", e)
                _error.emit("Error: ${e.localizedMessage}")
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
                Log.d(TAG, "Fetching favorite TV shows for account: $accountId")
                val response = RetrofitInstance.api.getFavoriteTVShows(
                    accountId = accountId,
                    apiKey = API_KEY,
                    sessionId = sessionId
                )
                
                if (response.isSuccessful) {
                    response.body()?.let { tvShowsResponse ->
                        // Ensure each TV show has the correct mediaType
                        val mappedShows = tvShowsResponse.results.map { item ->
                            Log.d(TAG, "Processing TV show: ${item.title ?: item.name} (ID: ${item.id})")
                            item.copy(mediaType = "tv")
                        }
                        _favoriteTVShows.value = mappedShows
                        Log.d(TAG, "Updated favorite TV shows. Count: ${mappedShows.size}")
                    }
                } else {
                    Log.e(TAG, "Error fetching favorite TV shows: ${response.code()} - ${response.message()}")
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching favorite TV shows", e)
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