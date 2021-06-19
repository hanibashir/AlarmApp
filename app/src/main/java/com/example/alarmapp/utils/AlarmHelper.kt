package com.example.alarmapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import com.example.alarmapp.broadcastreceivers.AlarmReceiver
import com.example.alarmapp.data.models.AlarmItem
import com.example.alarmapp.utils.Constants.Companion.ACTION_ALARM_RECEIVER
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME
import java.util.*

class AlarmHelper(private val context: Context) {

    // init alarm manager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // schedule alarm and return alarm item after updating it,
    // to update the database in MainFragment
    fun scheduleAlarm(alarmItem: AlarmItem): AlarmItem {

        // set calendar time
        val alarmDate = CalendarUtil().setCalendar(alarmItem.hour, alarmItem.minute)

        // if alarm time is passed add one day
        if (alarmDate.before(Calendar.getInstance())) alarmDate.add(Calendar.DATE, 1)

        // creating pending intent for alarm manager
        val pendingIntent = alarmPendingIntent(alarmItem)

        // to survive DOZE Mode on android M (API Level 23) and above we
        // need to call setExactAndAllowWhileIdle Method and
        // using AlarmManagerCompat to support all android versions
        AlarmManagerCompat.setAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            alarmDate.timeInMillis,
            pendingIntent
        )

        // change isScheduled field value before updating the alarm item in database
        alarmItem.isScheduled = true

        return alarmItem
    }

    // cancel alarm and return alarm item after updating it,
    // to update the database in MainFragment
    fun cancelAlarm(alarmItem: AlarmItem): AlarmItem {

        // cancel the pending intent
        alarmManager.cancel(alarmPendingIntent(alarmItem))

        alarmItem.isScheduled = false

        return alarmItem
    }

    // return alarm pending intent
    private fun alarmPendingIntent(alarmItem: AlarmItem): PendingIntent {
        // format alarm time and return it as string like: 08:00am or 20:00 depending
        // on the user device settings
        val alarmTimeString = CalendarUtil().formatCalendarTime(alarmItem.hour, alarmItem.minute)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_RECEIVER
            putExtra(ALARM_ID, alarmItem.alarmId)
            putExtra(ALARM_LABEL, alarmItem.alarmLabel)
            putExtra(ALARM_TIME, alarmTimeString)
        }

        val pendingIntentRequestCode = alarmItem.alarmId.toInt()

        return PendingIntent.getBroadcast(context, pendingIntentRequestCode, intent, 0)
    }



}