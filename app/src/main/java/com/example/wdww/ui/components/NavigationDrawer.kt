/**
 * Navigation Drawer Component.
 * This component implements a side navigation drawer that provides access to user-specific screens
 * such as favorite movies, TV shows, and alerts. It also includes a logout option.
 * The drawer uses Material3 design components for a modern and consistent look.
 */
package com.example.wdww.ui.components

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
import com.example.wdww.navigation.Screen

/**
 * A composable function that creates a navigation drawer with user-specific navigation options.
 * The drawer displays a list of screens for accessing user's favorite content and settings,
 * along with a logout option at the bottom.
 *
 * @param onNavigate Callback function triggered when a navigation item is selected, 
 *                  receives the selected Screen as parameter
 * @param onLogout Callback function triggered when the logout option is selected
 */
@Composable
fun NavigationDrawer(
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    // Define the list of screens to be displayed in the drawer
    val drawerScreens = listOf(
        Screen.MyMovies,
        Screen.MyTV,
        Screen.MyAlerts
    )

    // Main drawer container
    ModalDrawerSheet {
        // Top spacing
        Spacer(modifier = Modifier.height(16.dp))
        
        // Navigation items for user-specific screens
        drawerScreens.forEach { screen ->
            NavigationDrawerItem(
                label = { Text(screen.title) },
                selected = false,
                onClick = { onNavigate(screen) },
                icon = { 
                    // Assign appropriate icon based on screen type
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
        
        // Divider between navigation items and logout option
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Logout option at the bottom of the drawer
        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = onLogout,
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}