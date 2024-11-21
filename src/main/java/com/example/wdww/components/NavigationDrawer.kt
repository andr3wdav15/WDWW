package com.example.wdww.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.Screen

@Composable
fun NavigationDrawer(
    isOpen: Boolean,
    onClose: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    val drawerScreens = listOf(
        Screen.MyMovies,
        Screen.MyTV,
        Screen.MyAlerts
    )

    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))
        
        drawerScreens.forEach { screen ->
            NavigationDrawerItem(
                label = { Text(screen.title) },
                selected = false,
                onClick = { onNavigate(screen) },
                icon = { Icon(Icons.Default.Star, contentDescription = null) },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = onLogout,
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
} 