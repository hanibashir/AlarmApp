package com.example.alarmapp.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.alarmapp.R
import com.example.alarmapp.data.database.AlarmDatabase
import com.example.alarmapp.data.repository.AlarmRepository
import com.example.alarmapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.example.alarmapp.utils.Constants.Companion.CHANNEL_ID
import com.example.alarmapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "alarmService"

class AlarmService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // by default service run on UI thread, this can crash the app,
        // because of that we need to run it on separate thread
        Thread {
            //Log.d(CURRENT_THREAD_ID, "onStartCommand Thread: ${Thread.currentThread().id}")

            // check intent action
            when (intent?.action) {
                // if the intent contains this action, stop the service
                ACTION_STOP_SERVICE -> stopSelf()
                ACTION_START_SERVICE -> {
                    // get intent extra from broadcast receiver
                    val alarmName = intent.getStringExtra(ALARM_LABEL)
                    val alarmTime = intent.getStringExtra(ALARM_TIME)
                    val alarmId = intent.getLongExtra(ALARM_ID, 0)

                    // create notification
                    val notification = createNotification(alarmName, alarmTime)
                    

                    // AutoCancel does not work when service is still on foreground.
                    // Try remove service from foreground:
                    // startForeground(2, notification);
                    // stopForeground(false); //false - do not remove generated notification
                    // https://stackoverflow.com/a/51390532/10609832
                    startForeground(alarmId.toInt(), notification)
                    stopForeground(false)
                }
            }

        }.start()

        return START_NOT_STICKY
    }


    private fun createNotification(
        alarmName: String?,
        alarmTime: String?
    ): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(alarmName)
            .setContentText(alarmTime)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDeleteIntent(notificationPendingIntents())
            .setAutoCancel(true)
            .build()
    }

    // if the user dismissed the notification stop service
    private fun notificationPendingIntents(): PendingIntent {
        // if the notification is swiped or cleared stop service
        val deleteIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        return PendingIntent.getService(
            this,
            0,
            deleteIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null
}