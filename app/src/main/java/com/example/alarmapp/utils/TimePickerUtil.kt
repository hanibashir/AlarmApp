package com.example.alarmapp.utils

import android.os.Build
import android.widget.TimePicker

object TimePickerUtil {

    fun getTime(timePicker: TimePicker): Pair<Int, Int> {

        val hour: Int
        val minute: Int

        // if android version == M (API Level 23) and later ...
        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.hour
            minute = timePicker.minute
        }
        else {
            // we are targeting api level 21 so we need the following
            hour = timePicker.currentHour
            minute = timePicker.currentMinute
        }

        // return the two variables
        return Pair(hour, minute)
    }
}