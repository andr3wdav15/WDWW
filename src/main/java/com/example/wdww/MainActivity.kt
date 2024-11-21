package com.example.wdww

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.wdww.components.*
import com.example.wdww.screens.*
import com.example.wdww.ui.theme.WDWWTheme
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

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
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NavigationDrawer(
                    isOpen = drawerState.isOpen,
                    onClose = { 
                        scope.launch { drawerState.close() }
                    },
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
            Scaffold(
                topBar = {
                    SearchBar(
                        onMenuClick = { 
                            scope.launch { drawerState.open() }
                        },
                        onSearchTextChanged = { sharedViewModel.updateSearchQuery(it) },
                        searchTextState = searchQuery,
                        focusRequester = focusRequester,
                        navController = navController
                    )
                },
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
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