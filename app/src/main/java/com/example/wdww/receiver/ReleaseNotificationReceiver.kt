/**
 * Broadcast Receiver responsible for handling and displaying notifications
 * when movies or TV shows are released.
 *
 * This receiver creates and shows notifications using the Android NotificationManager
 * with support for both modern (Android O and above) and legacy Android versions.
 * It handles the creation of notification channels when required and displays
 * customized notifications for new media releases.
 */
package com.example.wdww.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.wdww.R

/**
 * BroadcastReceiver that handles the creation and display of notifications
 * for new media releases (movies and TV shows).
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
     *              - "posterPath": Path to the media poster (currently unused)
     *              - "mediaType": Type of media ("movie" by default)
     *
     * @suppress ObsoleteSdkInt - We need to support older Android versions
     */
    @SuppressLint("ObsoleteSdkInt")
    override fun onReceive(context: Context, intent: Intent) {
        val mediaId = intent.getIntExtra("mediaId", -1)
        val title = intent.getStringExtra("title") ?: return
        intent.getStringExtra("posterPath")
        intent.getStringExtra("mediaType") ?: "movie"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Release Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for movie and TV show releases"
            }
            notificationManager.createNotificationChannel(channel)
        }

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
        /**
         * Unique identifier for the notification channel.
         * Used to group notifications and manage notification settings on Android O and above.
         */
        const val CHANNEL_ID = "release_notifications"
    }
}
