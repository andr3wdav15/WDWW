// Screen.kt
package com.example.wdww

sealed class Screen(val route: String, val title: String) {
    object Trending : Screen("trending", "Trending")
    object Movies : Screen("movies", "Movies")
    object TVShows : Screen("tvshows", "TV Shows")
    object Theaters : Screen("theaters", "Theaters")
}
