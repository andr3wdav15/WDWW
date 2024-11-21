package com.example.wdww.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onMenuClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    searchTextState: String,
    focusRequester: FocusRequester,
    navController: NavController
) {
    var showKeyboard by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            TextField(
                value = searchTextState,
                onValueChange = { newText ->
                    onSearchTextChanged(newText)
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
                trailingIcon = {
                    IconButton(onClick = {
                        if (searchTextState.isNotEmpty()) {
                            navController.navigate("search")
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search Icon")
                    }
                },
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
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu Icon")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )

    LaunchedEffect(showKeyboard) {
        if (showKeyboard) {
            focusRequester.requestFocus()
        }
    }
}