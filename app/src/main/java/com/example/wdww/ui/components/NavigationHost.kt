/**
 * Navigation Host Component.
 * This component serves as the central navigation hub, managing the routing between different screens
 * in the application. It uses Jetpack Navigation Compose to handle screen transitions and maintains
 * the navigation stack.
 */
package com.example.wdww.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wdww.navigation.Screen
import com.example.wdww.screens.navbarscreens.MoviesScreen
import com.example.wdww.screens.navbarscreens.TVShowsScreen
import com.example.wdww.screens.navbarscreens.TheatersScreen
import com.example.wdww.screens.navbarscreens.TrendingScreen
import com.example.wdww.screens.navdrawerscreens.MyMoviesScreen
import com.example.wdww.screens.navdrawerscreens.MyTVShowsScreen
import com.example.wdww.screens.navdrawerscreens.MyAlertsScreen
import com.example.wdww.screens.discover.SearchScreen
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

/**
 * A composable function that sets up the navigation structure for the application.
 * It defines all possible navigation routes and maps them to their respective screen composables.
 * The navigation starts at the Trending screen by default.
 *
 * @param navController The navigation controller used to manage app navigation
 * @param modifier Modifier for customizing the layout
 * @param sharedViewModel ViewModel shared across screens for media-related data
 * @param authViewModel ViewModel for handling authentication state
 */
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(navController, startDestination = Screen.Trending.route, modifier = modifier) {
        // Main content screens
        composable(Screen.Trending.route) {
            TrendingScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }
        composable(Screen.Movies.route) {
            MoviesScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }
        composable(Screen.TVShows.route) {
            TVShowsScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }
        composable(Screen.Theaters.route) {
            TheatersScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }

        // User-specific screens
        composable(Screen.MyMovies.route) {
            MyMoviesScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }
        composable(Screen.MyTV.route) {
            MyTVShowsScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }
        composable(Screen.MyAlerts.route) {
            MyAlertsScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }

        // Utility screens
        composable("search") {
            SearchScreen(
                sharedViewModel = sharedViewModel,
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
