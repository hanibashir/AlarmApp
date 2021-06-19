package com.example.alarmapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.alarmapp.data.database.AlarmDatabase
import com.example.alarmapp.data.repository.AlarmRepository
import com.example.alarmapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ACTION_UPDATE_ALARM
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.example.alarmapp.utils.NotificationHelper
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
                    val alarmId = intent.getLongExtra(ALARM_ID, 0)
                    val alarmLabel = intent.getStringExtra(ALARM_LABEL)
                    val alarmTime = intent.getStringExtra(ALARM_TIME)


                    // create notification
                    val notification =
                        NotificationHelper(this).createNotification(alarmId, alarmLabel, alarmTime)
//                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                    manager.notify(alarmId.toInt(), notification)

                    updateAlarmItem(alarmId)

                    // AutoCancel does not work when service is still on foreground.
                    // Try remove service from foreground:
                    // startForeground(2, notification);
                    // stopForeground(false); //false - do not remove generated notification
                    // https://stackoverflow.com/a/51390532/10609832
                    startForeground(alarmId.toInt(), notification)
                    //stopForeground(false)
                }

            }

        }.start()

        return START_NOT_STICKY
    }

    private fun updateAlarmItem(alarmId: Long?) {

        Log.d(TAG, "update alarm")

        val dao = AlarmDatabase.getDatabaseInstance(application).alarmDao()
        val repository = AlarmRepository(dao)

        CoroutineScope(Dispatchers.IO).launch {
            repository.updateScheduled(false, alarmId!!)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}