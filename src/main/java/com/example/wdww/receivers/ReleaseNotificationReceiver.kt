package com.example.wdww.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.wdww.MainActivity
import com.example.wdww.R

class ReleaseNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val movieId = intent.getIntExtra("movieId", -1)
        val movieTitle = intent.getStringExtra("movieTitle") ?: return

        // Create an intent to open the app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            movieId,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, "RELEASE_NOTIFICATIONS")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Movie Release Tomorrow!")
            .setContentText("$movieTitle is releasing tomorrow!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(movieId, notification)
    }
}
