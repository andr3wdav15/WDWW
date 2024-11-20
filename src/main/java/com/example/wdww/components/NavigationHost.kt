package com.example.wdww.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wdww.Screen
import com.example.wdww.screens.MoviesScreen
import com.example.wdww.screens.TVShowsScreen
import com.example.wdww.screens.TheatersScreen
import com.example.wdww.screens.TrendingScreen
import com.example.wdww.viewmodel.SharedViewModel

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sharedViewModel: SharedViewModel = viewModel()

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
    }
}
