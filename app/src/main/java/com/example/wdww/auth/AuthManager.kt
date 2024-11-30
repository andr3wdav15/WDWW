/**
 * Authentication Manager for handling user session management.
 * This class provides functionality to store, retrieve, and manage user session data
 * using Android's DataStore Preferences.
 */
package com.example.wdww.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create a single DataStore instance for authentication preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/**
 * Manages authentication state and session handling for the application.
 * @property context The Android application context used to access DataStore
 */
class AuthManager(private val context: Context) {
    companion object {
        // Keys for storing session data in DataStore
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val SESSION_TIMESTAMP_KEY = longPreferencesKey("session_timestamp")
        // Session duration in milliseconds (1 hour)
        private const val SESSION_DURATION = 60 * 60 * 1000
    }

    /**
     * Flow that emits the current session ID if it exists and hasn't expired.
     * Returns null if the session has expired or doesn't exist.
     */
    val sessionId: Flow<String?> = context.dataStore.data.map { preferences ->
        val sessionId = preferences[SESSION_ID_KEY]
        val timestamp = preferences[SESSION_TIMESTAMP_KEY] ?: 0L
        
        if (isSessionExpired(timestamp)) {
            null
        } else {
            sessionId
        }
    }

    /**
     * Checks if the session has expired based on the timestamp.
     * @param timestamp The timestamp when the session was last updated
     * @return true if the session has expired, false otherwise
     */
    private fun isSessionExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > SESSION_DURATION
    }

    /**
     * Saves a new session ID along with the current timestamp.
     * @param sessionId The session ID to save
     */
    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_ID_KEY] = sessionId
            preferences[SESSION_TIMESTAMP_KEY] = System.currentTimeMillis()
        }
    }

    /**
     * Clears the current session data, effectively logging out the user.
     */
    suspend fun clearSessionId() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_ID_KEY)
            preferences.remove(SESSION_TIMESTAMP_KEY)
        }
    }
}