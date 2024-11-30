/**
 * Bottom Navigation Component.
 * This file contains the implementation of the bottom navigation bar using Jetpack Compose.
 */
package com.example.wdww.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.wdww.navigation.Screen
import androidx.compose.material.icons.filled.BarChart

/**
 * A composable function that creates a bottom navigation bar with different screen options.
 * 
 * @param navController The navigation controller used to handle screen navigation
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    // Define the list of available screens for navigation
    val screens = listOf(
        Screen.Trending,
        Screen.Movies,
        Screen.TVShows,
        Screen.Theaters
    )

    // Get the current navigation back stack entry to determine the active screen
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        // Create navigation items for each screen
        screens.forEach { screen ->
            NavigationBarItem(
                // Set the appropriate icon for each screen
                icon = {
                    when (screen) {
                        Screen.Trending -> Icon(
                            Icons.Default.BarChart,
                            contentDescription = screen.title
                        )
                        Screen.Movies -> Icon(
                            Icons.Default.Movie,
                            contentDescription = screen.title
                        )
                        Screen.TVShows -> Icon(
                            Icons.Default.Tv,
                            contentDescription = screen.title
                        )
                        Screen.Theaters -> Icon(
                            Icons.Default.Theaters,
                            contentDescription = screen.title
                        )
                        else -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )
                    }
                },
                // Display the screen title as the label
                label = { Text(screen.title) },
                // Highlight the currently selected screen
                selected = currentRoute == screen.route,
                // Handle navigation when an item is clicked
                onClick = {
                    navController.navigate(screen.route) {
                        // Configure navigation behavior
                        popUpTo(Screen.Trending.route) {
                            saveState = true  // Save the state when navigating away
                        }
                        launchSingleTop = true  // Avoid multiple copies of the same destination
                        restoreState = true  // Restore state when returning to a previously visited screen
                    }
                },
                // Configure the appearance of navigation items
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
