/**
 * Search Bar Component.
 * This component implements a top app bar with an integrated search functionality.
 * It features a text input field, search icon, and menu button, with automatic
 * navigation to the search screen when the user starts typing or initiates a search.
 */
package com.example.wdww.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController

/**
 * A composable function that creates a search bar within a top app bar.
 * Handles user input, keyboard interactions, and navigation to the search screen.
 * The search bar automatically navigates to the search screen when the user starts typing
 * or explicitly initiates a search.
 *
 * @param onMenuClick Callback function triggered when the menu icon is clicked
 * @param onSearchTextChanged Callback function triggered when search text changes
 * @param searchTextState Current state of the search text
 * @param focusRequester FocusRequester to manage focus on the search field
 * @param navController Navigation controller for handling screen transitions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onMenuClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    searchTextState: String,
    focusRequester: FocusRequester,
    navController: NavController
) {
    // State to track keyboard visibility
    var showKeyboard by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            // Search text field
            TextField(
                value = searchTextState,
                onValueChange = { newText ->
                    onSearchTextChanged(newText)
                    // Navigate to search screen when user starts typing
                    if (newText.isNotEmpty()) {
                        if (navController.currentDestination?.route != "search") {
                            navController.navigate("search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        showKeyboard = focusState.isFocused
                    },
                placeholder = { Text(text = "Search") },
                singleLine = true,
                // Search icon with click handling
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchTextState.isNotEmpty()) {
                            navController.navigate("search")
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search Icon")
                    }
                },
                // Keyboard configuration
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchTextState.isNotEmpty()) {
                            navController.navigate("search")
                        }
                    }
                ),
                // Visual styling
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        // Menu icon in the navigation area
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu Icon")
            }
        },
        // Top app bar styling
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )

    // Automatically request focus when keyboard should be shown
    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            focusRequester.requestFocus()
        }
    }
}