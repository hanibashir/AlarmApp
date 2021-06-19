package com.example.alarmapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.alarmapp.data.models.AlarmItem
import com.google.android.material.snackbar.Snackbar

object Messages {

    fun showScheduledMessage(rootView: View, alarmItem: AlarmItem?, state: String) {
        // we already set the calendar and saved to alarmDate variable, but after
        // we check if the time is passed, alarmDate variable value will not change,
        // so, we need to set the calendar again
        alarmItem?.let {
            val calendar = CalendarUtil().setCalendar(it.hour, it.minute, 0)
            it.alarmDay = CalendarUtil().getAlarmDay(calendar)
            val alarmTimeString = CalendarUtil().formatCalendarTime(it.hour, it.minute)
            createSnack(
                rootView,
                "Alarm $state for ${it.alarmDay} at: $alarmTimeString"
            )
        }
    }

    // create snack bar message
    fun createSnack(view: View, message: String) =
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()

    // create toast message
    private fun createToast(context: Context, text: String) =
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}