package com.example.alarmapp.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.alarmapp.services.AlarmService
import com.example.alarmapp.utils.Constants
import com.example.alarmapp.utils.Constants.Companion.ACTION_ALARM_RECEIVER
import com.example.alarmapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        when (intent?.action) {
            ACTION_ALARM_RECEIVER -> {
                val alarmName = intent.getStringExtra(ALARM_LABEL)
                val alarmTime = intent.getStringExtra(ALARM_TIME)
                val alarmId = intent.getLongExtra(ALARM_ID, 0)

                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    action = ACTION_START_SERVICE
                    putExtra(ALARM_LABEL, alarmName)
                    putExtra(ALARM_TIME, alarmTime)
                    putExtra(ALARM_ID, alarmId)
                }

                // start service
                ContextCompat.startForegroundService(context, serviceIntent)
            }

        }
    }
}