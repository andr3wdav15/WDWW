// Screen.kt
package com.example.wdww

sealed class Screen(val route: String, val title: String) {
    object Trending : Screen("trending", "Trending")
    object Movies : Screen("movies", "Movies")
    object TVShows : Screen("tvshows", "TV Shows")
    object Theaters : Screen("theaters", "Theaters")
    object MyMovies : Screen("my_movies", "My Movies")
    object MyTV : Screen("my_tv", "My TV")
    object MyAlerts : Screen("my_alerts", "My Alerts")
}
