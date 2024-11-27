package com.example.wdww.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wdww.components.MediaCard
import com.example.wdww.components.MediaDetailModal
import com.example.wdww.model.MediaItem
import com.example.wdww.viewmodel.AuthViewModel
import com.example.wdww.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAlertsScreen(
    sharedViewModel: SharedViewModel,
    authViewModel: AuthViewModel
) {
    val alerts by sharedViewModel.alerts.collectAsState()
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Alerts",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No alerts set. Add alerts for upcoming movies to see them here!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alerts.sortedBy { it.releaseDate }) { alert ->
                    val releaseDate = try {
                        LocalDate.parse(alert.releaseDate, DateTimeFormatter.ISO_DATE)
                    } catch (e: Exception) {
                        null
                    }
                    
                    MediaCard(
                        mediaItem = MediaItem(
                            id = alert.mediaId,
                            title = alert.title,
                            name = null,
                            overview = null,
                            posterPath = alert.posterPath,
                            releaseDate = alert.releaseDate,
                            firstAirDate = null,
                            mediaType = alert.mediaType,
                            voteAverage = null,
                            genreIds = null
                        ),
                        onClick = { selectedMedia = it },
                        badge = releaseDate?.let { date ->
                            val daysUntil = LocalDate.now().until(date).days
                            when {
                                daysUntil > 1 -> "$daysUntil days"
                                daysUntil == 1 -> "Tomorrow"
                                daysUntil == 0 -> "Today"
                                else -> null
                            }
                        }
                    )
                }
            }
        }
    }

    selectedMedia?.let { media ->
        MediaDetailModal(
            mediaItem = media,
            onDismiss = { selectedMedia = null },
            sharedViewModel = sharedViewModel,
            authViewModel = authViewModel
        )
    }
}