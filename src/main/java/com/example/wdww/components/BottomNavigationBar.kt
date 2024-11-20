package com.example.wdww.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.wdww.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(
        Screen.Trending,
        Screen.Movies,
        Screen.TVShows,
        Screen.Theaters
    )

    val currentDestinationRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    // Check if current route is in bottom nav screens
    val isBottomNavScreen = screens.any { it.route == currentDestinationRoute }

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Trending -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )
                        Screen.Movies -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )
                        Screen.TVShows -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )
                        Screen.Theaters -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )
                        else -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )
                    }
                },
                label = { Text(screen.title) },
                selected = currentDestinationRoute == screen.route,
                onClick = {
                    if (!isBottomNavScreen || currentDestinationRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}
