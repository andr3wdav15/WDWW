/**
 * Login screen implementation for WDWW app using Jetpack Compose.
 * Handles user authentication flow with TMDB, including:
 * - Initial login state with animated logo
 * - Authentication process with TMDB
 * - Loading states and error handling
 * - Success redirection
 */
package com.example.wdww.screens.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wdww.viewmodel.AuthState
import com.example.wdww.viewmodel.AuthViewModel

/**
 * Animated logo component that "breathes" by fading in and out.
 * Creates a visually engaging loading state using infinite animation.
 */
@Composable
private fun BreathingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Icon(
        imageVector = Icons.Filled.Theaters,
        contentDescription = "WDWW Logo",
        modifier = Modifier
            .size(200.dp)
            .padding(bottom = 32.dp),
        tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha)
    )
}

/**
 * Stylized sign-in button with elevation and press animation.
 * 
 * @param onClick Callback function triggered when the button is clicked
 */
@Composable
private fun StylishSignInButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale = if (isPressed) 0.95f else 1f
    
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .scale(scale)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 8.dp
        )
    ) {
        Text(
            "Sign In",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}

/**
 * Main login screen composable that handles the authentication flow.
 * 
 * This screen manages different states of the authentication process:
 * - Initial state with logo and sign-in button
 * - Loading state with progress indicator
 * - Authentication state that redirects to TMDB
 * - Error state with retry option
 * - Success state that triggers navigation
 *
 * @param authViewModel ViewModel handling authentication logic and state
 * @param onAuthSuccess Callback function triggered when authentication is successful
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            onAuthSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = authState) {
            is AuthState.Initial -> {
                BreathingLogo()
                StylishSignInButton(onClick = { authViewModel.startAuth() })
            }
            
            is AuthState.Loading -> {
                BreathingLogo()
            }
            
            is AuthState.RequiresUserAuth -> {
                LaunchedEffect(state.authUrl) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.authUrl))
                    context.startActivity(intent)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    BreathingLogo()
                    Text(
                        "Authenticating with TMDB...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            is AuthState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { authViewModel.startAuth() },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Retry")
                }
            }
            
            is AuthState.Authenticated -> {
                onAuthSuccess()
            }
        }
    }
}