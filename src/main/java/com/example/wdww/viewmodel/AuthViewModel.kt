package com.example.wdww.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wdww.auth.AuthManager
import com.example.wdww.model.auth.CreateSessionRequest
import com.example.wdww.model.auth.DeleteSessionRequest
import com.example.wdww.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authManager = AuthManager(application)
    private val authService = RetrofitInstance.authApi

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private var currentRequestToken: String? = null

    init {
        viewModelScope.launch {
            authManager.sessionId.collect { sessionId ->
                _isAuthenticated.value = !sessionId.isNullOrEmpty()
                if (_isAuthenticated.value) {
                    _authState.value = AuthState.Authenticated
                }
            }
        }
    }

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

    fun logout() {
        viewModelScope.launch {
            authManager.clearSessionId()
            _authState.value = AuthState.Initial
        }
    }

    suspend fun getSessionId(): String? {
        return authManager.sessionId.first()
    }

    companion object {
        private const val API_KEY = "c5479e7394cd551bad2a1af7e9bff8a3"
        private const val REDIRECT_URI = "wdww://auth"
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class RequiresUserAuth(val authUrl: String, val requestToken: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object Authenticated : AuthState()
} 