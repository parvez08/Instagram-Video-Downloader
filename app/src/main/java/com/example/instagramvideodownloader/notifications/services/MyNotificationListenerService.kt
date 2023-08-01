package com.example.instagramvideodownloader.notifications.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.instagramvideodownloader.notifications.NotificationsActivity

class MyNotificationListenerService : NotificationListenerService() {

    private val TAG = "NotificationListener"

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // This method will be called when a new notification is posted.
        val packageName = sbn.packageName
        val title = sbn.notification.extras.getString("android.title")
        val text = sbn.notification.extras.getString("android.text")

        val mainActivityIntent = Intent(this, NotificationsActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.putExtra("title", title)
        mainActivityIntent.putExtra("text", text)
        startActivity(mainActivityIntent)

        Log.d(TAG, "Notification posted - Package: $packageName, Title: $title, Text: $text")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // This method will be called when a notification is removed (e.g., dismissed by the user).
        val packageName = sbn.packageName
        val title = sbn.notification.extras.getString("android.title")
        val text = sbn.notification.extras.getString("android.text")

        Log.d(TAG, "Notification removed - Package: $packageName, Title: $title, Text: $text")
    }
}
