package com.example.wdww.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wdww.Screen
import com.example.wdww.screens.MoviesScreen
import com.example.wdww.screens.TVShowsScreen
import com.example.wdww.screens.TheatersScreen
import com.example.wdww.screens.TrendingScreen
import com.example.wdww.screens.MyMoviesScreen
import com.example.wdww.screens.MyTVScreen
import com.example.wdww.screens.MyAlertsScreen
import com.example.wdww.screens.SearchScreen
import com.example.wdww.viewmodel.SharedViewModel
import com.example.wdww.viewmodel.AuthViewModel

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(navController, startDestination = Screen.Trending.route, modifier = modifier) {
        composable(Screen.Trending.route) {
            TrendingScreen(sharedViewModel = sharedViewModel)
        }
        composable(Screen.Movies.route) {
            MoviesScreen(sharedViewModel = sharedViewModel)
        }
        composable(Screen.TVShows.route) {
            TVShowsScreen(sharedViewModel = sharedViewModel)
        }
        composable(Screen.Theaters.route) {
            TheatersScreen(sharedViewModel = sharedViewModel)
        }
        composable(Screen.MyMovies.route) {
            MyMoviesScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }
        composable(Screen.MyTV.route) {
            MyTVScreen(
                sharedViewModel = sharedViewModel,
                authViewModel = authViewModel
            )
        }
        composable(Screen.MyAlerts.route) {
            MyAlertsScreen(sharedViewModel = sharedViewModel)
        }
        composable("search") {
            SearchScreen(
                sharedViewModel = sharedViewModel,
                navController = navController
            )
        }
    }
}
