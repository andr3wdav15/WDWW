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
import android.util.Log
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

    // Authentication state management
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Authentication status
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    // User account ID
    private val _accountId = MutableStateFlow<Int?>(null)
    val accountId: StateFlow<Int?> = _accountId.asStateFlow()

    // Temporary storage for the current request token
    private var currentRequestToken: String? = null

    /**
     * Initialize the ViewModel by checking for existing session
     * and fetching account details if authenticated.
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
     * Initiates the authentication process by creating a request token
     * and preparing the authentication URL for user redirection.
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
     * Creates a session using an authenticated request token.
     * If successful, stores the session ID and updates authentication state.
     * 
     * @param requestToken The authenticated request token from TMDB
     */
    fun createSession(requestToken: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d("AuthViewModel", "Creating session with token: $requestToken")
                
                val response = authService.createSession(API_KEY, CreateSessionRequest(requestToken))
                if (response.isSuccessful && response.body()?.success == true) {
                    val sessionId = response.body()?.sessionId
                    if (!sessionId.isNullOrEmpty()) {
                        Log.d("AuthViewModel", "Session created successfully")
                        authManager.saveSessionId(sessionId)
                        _isAuthenticated.value = true
                        _authState.value = AuthState.Authenticated
                    } else {
                        Log.e("AuthViewModel", "Session ID was null or empty")
                        _authState.value = AuthState.Error("Invalid session ID")
                    }
                } else {
                    Log.e("AuthViewModel", "Failed to create session: ${response.errorBody()?.string()}")
                    _authState.value = AuthState.Error("Failed to create session")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error creating session", e)
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Logs out the user by clearing the stored session ID
     * and resetting authentication state.
     */
    fun logout() {
        viewModelScope.launch {
            authManager.clearSessionId()
            _authState.value = AuthState.Initial
        }
    }

    /**
     * Retrieves the current session ID.
     * 
     * @return The current session ID or null if not authenticated
     */
    suspend fun getSessionId(): String? {
        return authManager.sessionId.first()
    }

    /**
     * Fetches the user's account ID using the session ID.
     * Updates the accountId state flow with the result.
     * 
     * @param sessionId The current session ID
     */
    private suspend fun fetchAccountId(sessionId: String) {
        try {
            val response = NetworkClient.api.getAccountDetails(API_KEY, sessionId)
            if (response.isSuccessful && response.body()?.id != null) {
                _accountId.value = response.body()?.id
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error fetching account ID", e)
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
    
    /** State requiring user authentication with TMDB
     * @param authUrl URL for TMDB authentication
     * @param requestToken Token for the authentication request
     */
    data class RequiresUserAuth(val authUrl: String, val requestToken: String) : AuthState()
    
    /** Error state with error message
     * @param message Description of the error
     */
    data class Error(val message: String) : AuthState()
    
    /** Successfully authenticated state */
    object Authenticated : AuthState()
}