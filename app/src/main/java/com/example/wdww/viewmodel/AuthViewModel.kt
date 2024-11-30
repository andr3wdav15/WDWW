/**
 * ViewModel responsible for managing authentication state and operations with TMDB API.
 * 
 * This ViewModel handles:
 * - User authentication flow with TMDB
 * - Session management
 * - Account ID retrieval
 * - Authentication state management
 *
 * The authentication flow follows these steps:
 * 1. Create a request token
 * 2. Redirect user to TMDB for authentication
 * 3. Create a session with the authenticated token
 * 4. Store the session ID for future use
 */
package com.example.wdww.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wdww.auth.AuthManager
import com.example.wdww.model.auth.CreateSessionRequest
import com.example.wdww.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * AuthViewModel manages the authentication state and operations for the WDWW app.
 * 
 * @param application Application context required for AndroidViewModel
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authManager = AuthManager(application)
    private val authService = NetworkClient.authApi

    // StateFlow to track the current authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // StateFlow to track whether the user is authenticated
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    // StateFlow to store the user's TMDB account ID
    private val _accountId = MutableStateFlow<Int?>(null)
    val accountId: StateFlow<Int?> = _accountId.asStateFlow()

    // Stores the current request token during the auth flow
    private var currentRequestToken: String? = null

    /**
     * Initializes the ViewModel by setting up authentication state observers.
     * - Monitors the session ID from AuthManager
     * - Updates authentication state based on session ID presence
     * - Fetches account ID when session is valid
     */
    init {
        viewModelScope.launch {
            authManager.sessionId.collect { sessionId ->
                _isAuthenticated.value = !sessionId.isNullOrEmpty()
                if (_isAuthenticated.value) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Initial
                }
                
                if (sessionId != null) {
                    fetchAccountId(sessionId)
                } else {
                    _accountId.value = null 
                }
            }
        }
    }

    /**
     * Initiates the authentication process with TMDB.
     * Flow:
     * 1. Creates a request token from TMDB API
     * 2. If successful, prepares the auth URL for user login
     * 3. Updates auth state to require user authentication
     */
    fun startAuth() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = authService.createRequestToken(API_KEY)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val requestToken = response.body()?.requestToken
                    if (!requestToken.isNullOrEmpty()) {
                        currentRequestToken = requestToken
                        val authUrl = "https://www.themoviedb.org/authenticate/$requestToken?redirect_to=$REDIRECT_URI"
                        _authState.value = AuthState.RequiresUserAuth(authUrl, requestToken)
                    } else {
                        _authState.value = AuthState.Error("Invalid request token")
                    }
                } else {
                    _authState.value = AuthState.Error("Failed to create request token")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Creates a new session using an authenticated request token.
     * Called after user has approved the authentication request on TMDB.
     * 
     * @param requestToken The authenticated request token from TMDB
     */
    fun createSession(requestToken: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val response = authService.createSession(API_KEY, CreateSessionRequest(requestToken))
                if (response.isSuccessful && response.body()?.success == true) {
                    val sessionId = response.body()?.sessionId
                    if (!sessionId.isNullOrEmpty()) {
                        authManager.saveSessionId(sessionId)
                        _isAuthenticated.value = true
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error("Invalid session ID")
                    }
                } else {
                    _authState.value = AuthState.Error("Failed to create session")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Logs out the current user by clearing the session ID
     * and resetting the auth state to initial.
     */
    fun logout() {
        viewModelScope.launch {
            authManager.clearSessionId()
            _authState.value = AuthState.Initial
        }
    }

    /**
     * Retrieves the current session ID.
     * @return The current session ID or null if not authenticated
     */
    suspend fun getSessionId(): String? {
        return authManager.sessionId.first()
    }

    /**
     * Fetches the user's account ID from TMDB API.
     * Called automatically when a valid session is established.
     * 
     * @param sessionId The current session ID to use for the API request
     */
    private suspend fun fetchAccountId(sessionId: String) {
        try {
            val response = NetworkClient.api.getAccountDetails(API_KEY, sessionId)
            if (response.isSuccessful && response.body()?.id != null) {
                _accountId.value = response.body()?.id
            }
        } catch (_: Exception) {
            // Account ID fetch failure is handled silently
        }
    }

    companion object {
        private const val API_KEY = "c5479e7394cd551bad2a1af7e9bff8a3"
        private const val REDIRECT_URI = "wdww://auth"
    }
}

/**
 * Sealed class representing different states in the authentication flow.
 */
sealed class AuthState {
    /** Initial state before authentication process starts */
    object Initial : AuthState()
    
    /** Loading state during network operations */
    object Loading : AuthState()
    
    /** State requiring user authentication with TMDB */
    data class RequiresUserAuth(val authUrl: String, val requestToken: String) : AuthState()
    
    /** Error state with error message */
    data class Error(val message: String) : AuthState()
    
    /** Successfully authenticated state */
    object Authenticated : AuthState()
}