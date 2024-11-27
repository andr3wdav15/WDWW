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
import com.example.wdww.screens.MyTVShowsScreen
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
        composable("search") {
            SearchScreen(
                sharedViewModel = sharedViewModel,
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}
