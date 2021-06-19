package com.example.alarmapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.alarmapp.R
import com.example.alarmapp.services.AlarmService
import com.example.alarmapp.ui.activities.RingActivity
import com.example.alarmapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ACTION_UPDATE_ALARM
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.example.alarmapp.utils.Constants.Companion.CHANNEL_ID

class NotificationHelper(private val context: Context) {

    private val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) as Uri


    fun createNotification(
        alarmId: Long?,
        alarmLabel: String?,
        alarmTime: String?
    ): Notification {

        if (Build.VERSION.SDK_INT >= 26) {
            // create notification channel
            createNotificationChannel()
            // create notification
            return with(NotificationCompat.Builder(context, CHANNEL_ID)) {
                setSmallIcon(R.drawable.ic_notifications)
                setContentTitle(alarmLabel)
                setContentText(alarmTime)
                priority = NotificationCompat.PRIORITY_HIGH
                setSound(ringtoneUri)
                setFullScreenIntent(notificationPendingIntents(alarmId, alarmLabel, alarmTime).second, true) // full screen intent
                setDeleteIntent(notificationPendingIntents(alarmId, alarmLabel, alarmTime).first) // delete intent
                addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.dismiss),
                    notificationPendingIntents(alarmId, alarmLabel, alarmTime).first // update alarm intent
                )
                setAutoCancel(true)
            }.build()
        } else {
            return with(NotificationCompat.Builder(context, CHANNEL_ID)) {
                setSmallIcon(R.drawable.ic_notifications)
                setContentTitle(alarmLabel)
                setContentText(alarmTime)
                priority = NotificationCompat.PRIORITY_HIGH
                setSound(ringtoneUri)
                addAction(
                    R.drawable.ic_launcher_foreground,
                    context.getString(R.string.dismiss),
                    notificationPendingIntents(alarmId, alarmLabel, alarmTime).first // update alarm intent
                )
                setContentIntent(notificationPendingIntents(alarmId, alarmLabel, alarmTime).second) // // content intent
                setAutoCancel(true)
            }.build()
        }
    }


    // if the user dismissed the notification stop service
    private fun notificationPendingIntents(
        alarmId: Long?,
        alarmLabel: String?,
        alarmTime: String?
    ): Pair<PendingIntent, PendingIntent> {

        val stopServiceIntent = Intent(context, AlarmService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopServicePendingIntent = PendingIntent.getService(
            context,
            0,
            stopServiceIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val ringActivityIntent = Intent(context, RingActivity::class.java).apply {
            putExtra(ALARM_LABEL, alarmLabel)
            putExtra(ALARM_TIME, alarmTime)
            putExtra(ALARM_ID, alarmId)
        }
        val ringActivityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            ringActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return Pair(
            stopServicePendingIntent,
            ringActivityPendingIntent
        )
    }

    // create notification channel for Android version starting from Oreo (API Level 26)
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {

        val att = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Create the NotificationChannel
        val channelName = context.getString(R.string.alarm_app_channel_name)
        val channelDescription = context.getString(R.string.alarm_app_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, channelName, importance)
        // set channel values
        with(notificationChannel) {
            description = channelDescription
            enableVibration(true)
            setSound(ringtoneUri, att)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            setShowBadge(true)
            setBypassDnd(true)
        }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}