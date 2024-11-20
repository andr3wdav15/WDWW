package com.example.wdww

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.wdww.components.BottomNavigationBar
import com.example.wdww.components.NavigationHost
import com.example.wdww.components.SearchBar
import com.example.wdww.ui.theme.WDWWTheme
import com.example.wdww.viewmodel.SharedViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WDWWTheme {
                val navController = rememberNavController()
                val sharedViewModel: SharedViewModel = viewModel()
                val focusRequester = remember { FocusRequester() }
                val keyboardController = LocalSoftwareKeyboardController.current
                val focusManager = LocalFocusManager.current

                Box(modifier = Modifier.clickable {
                    focusRequester.freeFocus()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }) {
                    Scaffold(
                        topBar = {
                            SearchBar(
                                onMenuClick = {
                                },
                                onSearchTextChanged = { newText ->
                                    sharedViewModel.updateSearchQuery(newText)
                                },
                                searchTextState = sharedViewModel.searchQuery.collectAsState().value,
                                focusRequester = focusRequester
                            )
                        },
                        bottomBar = { BottomNavigationBar(navController) }
                    ) { innerPadding ->
                        NavigationHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}