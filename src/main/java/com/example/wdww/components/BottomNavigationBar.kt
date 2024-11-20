package com.example.wdww.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
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

    NavigationBar {
        val currentBackStackEntry = navController.currentBackStackEntryAsState()
        val currentDestinationRoute = currentBackStackEntry.value?.destination?.route

        screens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        is Screen.Trending -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )

                        is Screen.Movies -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )

                        is Screen.TVShows -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )

                        is Screen.Theaters -> Icon(
                            Icons.Default.Star,
                            contentDescription = screen.title
                        )
                    }
                },
                label = { Text(screen.title) },
                selected = currentDestinationRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
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
