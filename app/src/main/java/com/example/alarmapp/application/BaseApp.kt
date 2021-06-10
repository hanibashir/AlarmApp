package com.example.alarmapp.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.alarmapp.utils.Constants.Companion.CHANNEL_ID

class BaseApp: Application() {

    override fun onCreate() {
        super.onCreate()

        // create notification channel for Android version starting from Oreo (API Level 26)
        if (Build.VERSION.SDK_INT >= 26) {
            val notification = NotificationChannel(
                CHANNEL_ID,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notification)
        }
    }

}