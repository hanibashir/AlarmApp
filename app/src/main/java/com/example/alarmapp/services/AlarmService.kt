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
import com.example.alarmapp.ui.activities.RingActivity
import com.example.alarmapp.utils.Constants
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

                    updateAlarmItem(alarmId)

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

    private fun updateAlarmItem(alarmId: Long) {
        val dao = AlarmDatabase.getDatabaseInstance(application).alarmDao()
        val repository = AlarmRepository(dao)

        CoroutineScope(Dispatchers.IO).launch {
            repository.updateScheduled(false, alarmId)
        }

    }

    private fun createNotification(
        alarmName: String?,
        alarmTime: String?
    ): Notification {

        // notification intent
        val notificationIntent = Intent(this, RingActivity::class.java).apply {
            putExtra(ALARM_LABEL, alarmName)
            putExtra(ALARM_TIME, alarmTime)
        }

        // we need two pending intents to ship with notification,
        // one to open RingActivity, and the other to stop the service if
        // the notification is swiped or cleared. so,  we create them in
        // separate method to keep the code clean and use the return values of this method.
        val (ringPendingIntent, deletePendingIntent) = notificationPendingIntents(
            notificationIntent
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(alarmName)
            .setContentText(alarmTime)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(ringPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setAutoCancel(true)
            .build()
    }

    // create two pending intents. one if the user tapped on the notification message,
    // and the other if the dismissed the notification
    private fun notificationPendingIntents(
        notificationIntent: Intent
    ): Pair<PendingIntent,PendingIntent> {
        // when notification clicked get RingActivity
        // use update flag to only update the current intent extra if
        // the intent is present and not replace it
        val ringPendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // if the notification is swiped or cleared
        val deleteIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val deletePendingIntent = PendingIntent.getService(
            this,
            0,
            deleteIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        return Pair(ringPendingIntent, deletePendingIntent)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}