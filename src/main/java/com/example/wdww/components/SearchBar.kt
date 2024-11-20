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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onMenuClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    searchTextState: String,
    focusRequester: FocusRequester
) {
    var showKeyboard by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            TextField(
                value = searchTextState,
                onValueChange = onSearchTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        showKeyboard = focusState.isFocused
                    },
                placeholder = { Text(text = "Search") },
                singleLine = true,
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search Icon")
                },
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