/**
 * Broadcast Receiver responsible for handling and displaying notifications
 * when movies or TV shows are released.
 *
 * This receiver creates and shows notifications using the Android NotificationManager
 * with support for both modern and legacy Android versions.
 */
package com.example.wdww.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.wdww.R

/**
 * BroadcastReceiver that handles the creation and display of notifications
 * for new media releases.
 */
class ReleaseNotificationReceiver : BroadcastReceiver() {
    /**
     * Handles incoming broadcast intents for new media releases.
     * Creates and displays a notification with the release information.
     *
     * @param context The Context in which the receiver is running
     * @param intent The Intent being received, containing media details:
     *              - "mediaId": Unique identifier for the media (used as notification ID)
     *              - "title": Title of the movie or TV show
     */
    override fun onReceive(context: Context, intent: Intent) {
        val mediaId = intent.getIntExtra("mediaId", -1)
        val title = intent.getStringExtra("title") ?: return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Release Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for movie and TV show releases"
        }
        notificationManager.createNotificationChannel(channel)

        // Build and display the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Release Available!")
            .setContentText("$title is now available to watch")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(mediaId, notification)
    }

    companion object {
        const val CHANNEL_ID = "release_notifications"
    }
}
