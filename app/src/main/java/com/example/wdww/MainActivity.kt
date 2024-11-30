/**
 * MainActivity serves as the entry point and main container for the WDWW app.
 * 
 * This activity handles:
 * - Authentication flow and deep linking
 * - Navigation between screens
 * - Responsive layout management
 * - Permission requests for notifications
 * - ViewModels initialization
 *
 * The app supports:
 * - Portrait and landscape orientations
 * - Edge-to-edge design with system bars
 * - Navigation drawer for menu access
 * - Bottom navigation for quick access
 * - Search functionality
 */
package com.example.wdww

import android.content.Intent
import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.wdww.ui.theme.WDWWTheme
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import android.app.AlarmManager
import android.os.Build
import android.provider.Settings
import com.example.wdww.navigation.Screen
import com.example.wdww.screens.auth.LoginScreen
import com.example.wdww.ui.components.BottomNavigationBar
import com.example.wdww.ui.components.NavigationDrawer
import com.example.wdww.ui.components.NavigationHost
import com.example.wdww.ui.components.SearchBar

/**
 * MainActivity is the single activity container for the WDWW app.
 * It manages authentication state, navigation, and system permissions.
 */
class MainActivity : ComponentActivity() {
    // ViewModels
    private val authViewModel: AuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SharedViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    /**
     * Permission launcher for notification permissions on Android 13+
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.w("MainActivity", "Notification permission denied")
        }
    }

    /**
     * Handles deep link intents for authentication callback
     */
    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val uri = intent.data
            if (uri?.host == "auth") {
                val requestToken = uri.getQueryParameter("request_token")
                if (!requestToken.isNullOrEmpty()) {
                    authViewModel.createSession(requestToken)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Request exact alarm permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }

        // Handle deep link intent if activity is newly created
        if (savedInstanceState == null) {
            handleIntent(intent)
        }
        
        setContent {
            WDWWTheme {
                val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

                if (!isAuthenticated) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onAuthSuccess = { }
                    )
                } else {
                    MainContent(
                        authViewModel = authViewModel,
                        sharedViewModel = sharedViewModel
                    )
                }
            }
        }
    }
}

/**
 * Main content container for the authenticated user interface.
 * Manages navigation, search, and responsive layout.
 *
 * @param authViewModel ViewModel for authentication state
 * @param sharedViewModel ViewModel for shared app state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    authViewModel: AuthViewModel,
    sharedViewModel: SharedViewModel
) {
    val navController = rememberNavController()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val searchQuery by sharedViewModel.searchQuery.collectAsState()

    // Track device orientation for responsive layout
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // Root container with keyboard dismiss handling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            }
    ) {
        // Navigation drawer for menu access
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavigationDrawer(
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Trending.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    onLogout = {
                        authViewModel.logout()
                    }
                )
            }
        ) {
            // Main scaffold with responsive layout components
            Scaffold(
                topBar = {
                    if (isPortrait) {
                        SearchBar(
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            },
                            onSearchTextChanged = { sharedViewModel.updateSearchQuery(it) },
                            searchTextState = searchQuery,
                            focusRequester = focusRequester,
                            navController = navController
                        )
                    }
                },
                bottomBar = {
                    if (isPortrait) {
                        BottomNavigationBar(navController = navController)
                    }
                }
            ) { paddingValues ->
                // Content area with dynamic padding based on orientation
                Box(
                    modifier = Modifier.padding(
                        PaddingValues(
                            top = if (!isPortrait || navController.currentDestination?.route == "search") 0.dp 
                                  else paddingValues.calculateTopPadding(),
                            bottom = if (isPortrait) paddingValues.calculateBottomPadding() else 0.dp,
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                        )
                    )
                ) {
                    NavigationHost(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}