package com.example.wdww.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
        Text(
            text = "My Lists",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        
        drawerScreens.forEach { screen ->
            NavigationDrawerItem(
                label = { Text(screen.title) },
                selected = false,
                onClick = {
                    onNavigate(screen)
                    onClose()
                },
                icon = {
                    when (screen) {
                        Screen.MyMovies -> Icon(Icons.Default.Star, contentDescription = null)
                        Screen.MyTV -> Icon(Icons.Default.Star, contentDescription = null)
                        Screen.MyAlerts -> Icon(Icons.Default.Star, contentDescription = null)
                        else -> Icon(Icons.Default.Star, contentDescription = null)
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = {
                onLogout()
                onClose()
            },
            icon = {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Logout"
                )
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
} 