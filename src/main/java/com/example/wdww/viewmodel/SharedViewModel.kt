package com.example.wdww.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wdww.network.AddToFavoritesRequest
import com.example.wdww.model.MediaDetails
import com.example.wdww.model.MediaItem
import com.example.wdww.model.Alert
import com.example.wdww.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wdww.model.AddMediaToListRequest
import com.example.wdww.model.CreateListRequest
import com.example.wdww.receivers.ReleaseNotificationReceiver
import java.time.LocalDate
import java.time.ZoneId

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "SharedViewModel"
        private const val PREFS_NAME = "WDWWPrefs"
        private const val THEATRE_LIST_ID_KEY = "theatre_list_id"
        private const val API_KEY = "c5479e7394cd551bad2a1af7e9bff8a3"
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)

    private val _selectedMediaDetails = MutableStateFlow<MediaDetails?>(null)
    val selectedMediaDetails: StateFlow<MediaDetails?> = _selectedMediaDetails

    private val _favoriteMovies = MutableStateFlow<List<MediaItem>>(emptyList())
    val favoriteMovies: StateFlow<List<MediaItem>> = _favoriteMovies

    private val _favoriteTVShows = MutableStateFlow<List<MediaItem>>(emptyList())
    val favoriteTVShows: StateFlow<List<MediaItem>> = _favoriteTVShows

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _theatreListItems = MutableStateFlow<List<MediaItem>>(emptyList())

    private var theatreListId: Int? = null
        set(value) {
            field = value
            // Persist to SharedPreferences
            value?.let { id ->
                getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putInt(THEATRE_LIST_ID_KEY, id)
                    .apply()
            }
        }

    init {
        // Load theatre list ID from SharedPreferences
        theatreListId = getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(THEATRE_LIST_ID_KEY, -1)
            .takeIf { it != -1 }
    }

    fun createTheatreList(sessionId: String) {
        viewModelScope.launch {
            try {
                val request = CreateListRequest(
                    name = "Theatre Notifications",
                    description = "Movies I want to be notified about when they hit theatres"
                )
                val response = RetrofitInstance.api.createList(API_KEY, sessionId, request)
                if (response.isSuccessful && response.body()?.success == true) {
                    theatreListId = response.body()?.list_id
                    Log.d(TAG, "Created theatre list with ID: $theatreListId")
                } else {
                    Log.e(TAG, "Failed to create theatre list: ${response.errorBody()?.string()}")
                    _error.value = "Failed to create theatre list"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating theatre list", e)
                _error.value = "Error creating theatre list: ${e.localizedMessage}"
            }
        }
    }

    suspend fun getTheatreList(accountId: Int, sessionId: String): Boolean {
        return try {
            Log.d(TAG, "Getting theatre list for account $accountId")
            
            // Clean up duplicate lists first
            val listsResponse = RetrofitInstance.api.getLists(accountId, API_KEY, sessionId)
            Log.d(TAG, "Lists response: ${listsResponse.raw().request.url}")
            if (!listsResponse.isSuccessful) {
                Log.e(TAG, "Failed to get lists: ${listsResponse.errorBody()?.string()}")
                _error.value = "Failed to get lists"
                return false
            }

            val theatreLists = listsResponse.body()?.results?.filter { it.name == "Theatre Notifications" }
            Log.d(TAG, "Found ${theatreLists?.size ?: 0} theatre lists")

            // Keep the newest list if it exists
            theatreLists?.maxByOrNull { it.id }?.let {
                theatreListId = it.id
                Log.d(TAG, "Using existing theatre list with ID: ${it.id}")
            } ?: run {
                // If no theatre list exists, create one
                Log.d(TAG, "No theatre list found, creating new one")
                val request = CreateListRequest(
                    name = "Theatre Notifications",
                    description = "Movies I want to be notified about when they hit theatres"
                )
                val createResponse = RetrofitInstance.api.createList(API_KEY, sessionId, request)
                if (createResponse.isSuccessful && createResponse.body()?.success == true) {
                    theatreListId = createResponse.body()?.list_id
                    Log.d(TAG, "Created new theatre list with ID: ${createResponse.body()?.list_id}")
                } else {
                    Log.e(TAG, "Failed to create theatre list: ${createResponse.errorBody()?.string()}")
                    _error.value = "Failed to create theatre list"
                    return false
                }
            }

            // Get the list items
            theatreListId?.let { listId ->
                Log.d(TAG, "Fetching theatre list with ID: $listId")
                val response = RetrofitInstance.api.getList(listId, API_KEY)
                Log.d(TAG, "List response URL: ${response.raw().request.url}")
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d(TAG, "Response body: $responseBody")
                    
                    val listItems = responseBody?.results
                    if (listItems != null) {
                        Log.d(TAG, "Successfully fetched theatre list with ${listItems.size} items")
                        _theatreListItems.value = listItems
                        syncAlertsWithTheatreList(listItems)
                        true
                    } else {
                        Log.e(TAG, "Theatre list response body is null")
                        _error.value = "Failed to get theatre list items"
                        false
                    }
                } else {
                    Log.e(TAG, "Failed to get theatre list: ${response.code()} - ${response.errorBody()?.string()}")
                    _error.value = "Failed to get theatre list"
                    false
                }
            } ?: run {
                Log.e(TAG, "Theatre list ID is null after all attempts")
                _error.value = "Failed to get theatre list ID"
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting theatre list", e)
            _error.value = "Error getting theatre list: ${e.localizedMessage}"
            false
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun fetchMediaDetails(mediaId: Int, mediaType: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d(TAG, "Fetching details for $mediaType with ID: $mediaId")
                val response = RetrofitInstance.api.getMediaDetails(
                    mediaType = mediaType,
                    mediaId = mediaId,
                    apiKey = API_KEY,
                    appendToResponse = "credits"
                )
                
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
                    posterPath = mediaDetails?.posterPath,
                    backdropPath = mediaDetails?.backdropPath,
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

    fun addToTheatreList(mediaId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                theatreListId?.let { listId ->
                    val request = AddMediaToListRequest(mediaId)
                    val response = RetrofitInstance.api.addToList(listId, API_KEY, sessionId, request)
                    if (response.isSuccessful) {
                        Log.d(TAG, "Added media $mediaId to theatre list")
                    } else {
                        Log.e(TAG, "Failed to add to theatre list: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding to theatre list", e)
            }
        }
    }

    fun removeFromTheatreList(mediaId: Int, sessionId: String) {
        viewModelScope.launch {
            try {
                theatreListId?.let { listId ->
                    val request = AddMediaToListRequest(mediaId)
                    val response = RetrofitInstance.api.removeFromList(listId, API_KEY, sessionId, request)
                    if (response.isSuccessful) {
                        Log.d(TAG, "Removed media $mediaId from theatre list")
                    } else {
                        Log.e(TAG, "Failed to remove from theatre list: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing from theatre list", e)
            }
        }
    }

    private fun syncAlertsWithTheatreList(listItems: List<MediaItem>) {
        if (listItems.isEmpty()) {
            Log.d(TAG, "Theatre list is empty, no alerts to sync")
            return
        }

        Log.d(TAG, "Starting syncAlertsWithTheatreList with ${listItems.size} items")
        viewModelScope.launch {
            try {
                // Get current alerts, initialize if null
                val currentAlerts = _alerts.value ?: emptyList()
                Log.d(TAG, "Current alerts: ${currentAlerts.size}")

                // Create alerts for any movies in the theatre list that aren't in local alerts
                val newAlerts = listItems.mapNotNull { item ->
                    // Only create alert if we have required fields
                    val title = item.title ?: item.name
                    val releaseDate = item.releaseDate ?: item.firstAirDate
                    
                    if (title != null && releaseDate != null && !currentAlerts.any { it.mediaId == item.id }) {
                        Log.d(TAG, "Creating new alert for: $title")
                        Alert(
                            mediaId = item.id,
                            title = title,
                            releaseDate = releaseDate,
                            posterPath = item.posterPath ?: "",
                            mediaType = item.mediaType ?: "movie"
                        )
                    } else {
                        if (title == null || releaseDate == null) {
                            Log.w(TAG, "Skipping item ${item.id} - missing title or release date")
                        } else {
                            Log.d(TAG, "Alert already exists for: $title")
                        }
                        null
                    }
                }
                
                if (newAlerts.isNotEmpty()) {
                    val updatedAlerts = currentAlerts + newAlerts
                    Log.d(TAG, "Adding ${newAlerts.size} new alerts. Total alerts: ${updatedAlerts.size}")
                    _alerts.value = updatedAlerts
                } else {
                    Log.d(TAG, "No new alerts to add")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing alerts with theatre list", e)
                _error.value = "Error syncing alerts: ${e.localizedMessage}"
            }
        }
    }

    fun addAlert(
        context: Context,
        movieId: Int,
        movieTitle: String,
        releaseDate: String,
        posterPath: String?,  
        mediaType: String,
        sessionId: String? = null
    ) {
        viewModelScope.launch {
            try {
                // Create alert object
                val alert = Alert(
                    mediaId = movieId,
                    title = movieTitle,
                    releaseDate = releaseDate,
                    posterPath = posterPath ?: "",  
                    mediaType = mediaType
                )

                // Update alerts list
                _alerts.value = _alerts.value + alert

                // Add to TMDB list if session ID is provided
                sessionId?.let { sid ->
                    if (theatreListId == null) {
                        createTheatreList(sid)
                    }
                    theatreListId?.let { listId ->
                        addToTheatreList(movieId, sid)
                    }
                }

                // Schedule notification
                scheduleNotification(context, alert)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding alert", e)
                _error.value = "Failed to add alert: ${e.message}"
            }
        }
    }

    private fun scheduleNotification(context: Context, alert: Alert) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReleaseNotificationReceiver::class.java).apply {
            putExtra("mediaId", alert.mediaId)
            putExtra("title", alert.title)
            putExtra("posterPath", alert.posterPath)
            putExtra("mediaType", alert.mediaType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.mediaId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val releaseDate = LocalDate.parse(alert.releaseDate)
            val notificationTime = releaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notificationTime,
                pendingIntent
            )
            
            Log.d(TAG, "Scheduled notification for ${alert.title} on ${alert.releaseDate}")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling notification", e)
            _error.value = "Failed to schedule notification: ${e.message}"
        }
    }

    fun removeAlert(context: Context, movieId: Int) {
        try {
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
            
            Log.d(TAG, "Successfully removed alert and cancelled notification")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to remove alert", e)
            _error.value = "Failed to remove alert: ${e.localizedMessage}"
        }
    }

    fun isAlertSet(movieId: Int): Boolean {
        return _alerts.value.any { it.mediaId == movieId }
    }
}