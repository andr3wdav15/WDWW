/**
 * Screen defines the navigation destinations within the WDWW app.
 * 
 * This sealed class represents all possible navigation routes in the app,
 * ensuring type-safe navigation between screens. Each screen object contains:
 * - route: Unique identifier for navigation
 * - title: Display name for the screen
 *
 * Navigation structure:
 * - Trending: Home screen showing trending content
 * - Movies: Browse and search movies
 * - TV Shows: Browse and search TV shows
 * - Theaters: Movies in theaters and upcoming releases
 * - My Movies: User's favorite movies
 * - My TV: User's favorite TV shows
 * - My Alerts: Release date notifications
 */
package com.example.wdww.navigation

/**
 * Sealed class defining all navigation destinations in the app.
 *
 * @property route Unique route identifier for navigation
 * @property title Display name shown in UI elements
 */
sealed class Screen(val route: String, val title: String) {
    /**
     * Home screen showing trending movies and TV shows
     */
    object Trending : Screen("trending", "Trending")

    /**
     * Browse and search movies by genre and streaming service
     */
    object Movies : Screen("movies", "Movies")

    /**
     * Browse and search TV shows by genre and streaming service
     */
    object TVShows : Screen("tvshows", "TV Shows")

    /**
     * View movies currently in theaters and upcoming releases
     */
    object Theaters : Screen("theaters", "Theaters")

    /**
     * User's favorite and watchlist movies
     */
    object MyMovies : Screen("my_movies", "My Movies")

    /**
     * User's favorite and watchlist TV shows
     */
    object MyTV : Screen("my_tv", "My TV")

    /**
     * Release date notifications and alerts
     */
    object MyAlerts : Screen("my_alerts", "My Alerts")
}
