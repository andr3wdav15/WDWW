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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthManager(private val context: Context) {
    companion object {
        private val SESSION_ID_KEY = stringPreferencesKey("session_id")
        private val SESSION_TIMESTAMP_KEY = longPreferencesKey("session_timestamp")
        private const val SESSION_DURATION = 60 * 60 * 1000 // 60 minutes in milliseconds
    }

    val sessionId: Flow<String?> = context.dataStore.data.map { preferences ->
        val sessionId = preferences[SESSION_ID_KEY]
        val timestamp = preferences[SESSION_TIMESTAMP_KEY] ?: 0L
        
        if (isSessionExpired(timestamp)) {
            null
        } else {
            sessionId
        }
    }

    private fun isSessionExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > SESSION_DURATION
    }

    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_ID_KEY] = sessionId
            preferences[SESSION_TIMESTAMP_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun clearSessionId() {
        context.dataStore.edit { preferences ->
            preferences.remove(SESSION_ID_KEY)
            preferences.remove(SESSION_TIMESTAMP_KEY)
        }
    }
} 