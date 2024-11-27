package com.example.wdww.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
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
                icon = { 
                    when (screen) {
                        Screen.MyMovies -> Icon(Icons.Default.Movie, contentDescription = screen.title)
                        Screen.MyTV -> Icon(Icons.Default.Tv, contentDescription = screen.title)
                        Screen.MyAlerts -> Icon(Icons.Default.Notifications, contentDescription = screen.title)
                        else -> Icon(Icons.Default.Star, contentDescription = screen.title)
                    }
                },
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
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
} 