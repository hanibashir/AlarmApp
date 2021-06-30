package com.example.alarmapp.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.fragment.app.FragmentActivity
import com.example.alarmapp.R
import com.example.alarmapp.broadcastreceivers.AlarmReceiver
import com.example.alarmapp.data.AlarmItem
import com.example.alarmapp.services.TAG
import com.example.alarmapp.ui.fragments.SnoozeDismissFragment
import com.example.alarmapp.utils.CalendarUtil
import com.example.alarmapp.utils.Constants.Companion.ACTION_ALARM_RECEIVER
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.example.alarmapp.utils.Constants.Companion.FRIDAY
import com.example.alarmapp.utils.Constants.Companion.MONDAY
import com.example.alarmapp.utils.Constants.Companion.REPEATING
import com.example.alarmapp.utils.Constants.Companion.SATURDAY
import com.example.alarmapp.utils.Constants.Companion.SNOOZE_TIME
import com.example.alarmapp.utils.Constants.Companion.SUNDAY
import com.example.alarmapp.utils.Constants.Companion.THURSDAY
import com.example.alarmapp.utils.Constants.Companion.TUESDAY
import com.example.alarmapp.utils.Constants.Companion.WEDNESDAY
import java.util.*

class AlarmHelper(private val context: Context) {

    // init alarm manager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val calendarUtil = CalendarUtil()

    // schedule alarm and return alarm item after updating it,
    // to update the database in MainFragment
    fun scheduleAlarm(alarmItem: AlarmItem): AlarmItem {

        // set calendar time
        val alarmDate = calendarUtil.setCalendar(alarmItem)

        // if alarm time is passed add one day
        if (alarmDate.before(Calendar.getInstance())) alarmDate.add(Calendar.DATE, 1)

        // creating pending intent for alarm manager
        val pendingIntent = alarmPendingIntent(alarmItem)

        if (alarmItem.isRepeating) { // if is repeating alarm
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmDate.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } else {
            // is a single day alarm
            // to survive DOZE Mode on android M (API Level 23) and above we
            // need to call setExactAndAllowWhileIdle Method and
            // use AlarmManagerCompat class to support old android versions
            AlarmManagerCompat.setAlarmClock(
                alarmManager,
                alarmDate.timeInMillis,
                pendingIntent,
                pendingIntent
            )
        }

        // change isScheduled field value before updating the alarm item in database
        alarmItem.isScheduled = true

        return alarmItem
    }

    // cancel alarm and return alarm item after updating it,
    // to update the database in MainFragment
    fun cancelAlarm(alarmItem: AlarmItem): AlarmItem {
        // cancel the pending intent
        alarmManager.cancel(alarmPendingIntent(alarmItem))
        // change isScheduled field value before updating the alarm item in database
        alarmItem.isScheduled = false
        return alarmItem
    }

    // return alarm pending intent
    private fun alarmPendingIntent(alarmItem: AlarmItem): PendingIntent {
        // format alarm time and return it as string like: 08:00am or 20:00 depending
        // on the user device settings
        val alarmTimeString = calendarUtil.formatCalendarTime(alarmItem)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_RECEIVER
            putExtra(ALARM_ID, alarmItem.alarmId)
            putExtra(ALARM_LABEL, alarmItem.alarmLabel)
            putExtra(ALARM_TIME, alarmTimeString)
            // repeating days extra
            putExtra(REPEATING, alarmItem.isRepeating)
            putExtra(MONDAY, alarmItem.isMonday)
            putExtra(TUESDAY, alarmItem.isTuesday)
            putExtra(WEDNESDAY, alarmItem.isWednesday)
            putExtra(THURSDAY, alarmItem.isThursday)
            putExtra(FRIDAY, alarmItem.isFriday)
            putExtra(SATURDAY, alarmItem.isSaturday)
            putExtra(SUNDAY, alarmItem.isSunday)
        }

        val pendingIntentRequestCode = alarmItem.alarmId.toInt()

        // need to use (PendingIntent.FLAG_UPDATE_CURRENT) flag for edit alarm case
        return PendingIntent.getBroadcast(
            context,
            pendingIntentRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // alarm snoozed
    fun snooze(snoozeTime: Int, alarmItem: AlarmItem): AlarmItem {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, snoozeTime)

        // update alarm item time
        alarmItem.hour = calendar.get(Calendar.HOUR_OF_DAY)
        alarmItem.minute = calendar.get(Calendar.MINUTE)

        scheduleAlarm(alarmItem)

        return alarmItem
    }

    // alarm dismissed
    fun dismiss(alarmItem: AlarmItem): AlarmItem {

        if (!alarmItem.isRepeating)
            alarmItem.isScheduled = false
        return alarmItem
    }
}