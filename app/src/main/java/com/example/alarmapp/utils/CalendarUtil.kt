package com.example.alarmapp.utils

import java.text.DateFormat
import java.util.*


private const val TAG = "calendarUtil"

class CalendarUtil {

    fun setCalendar(hour: Int, minute: Int, second: Int = 0): Calendar {

        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
        }
    }

    fun formatCalendarTime(hour: Int, minute: Int): String =
        DateFormat.getTimeInstance(DateFormat.SHORT).format(setCalendar(hour, minute).time)

    fun getAlarmDay(alarmDate: Calendar) =
        if (alarmDate.before(Calendar.getInstance())) { // if alarm time already passed
            // add one day to the calendar
            alarmDate.add(Calendar.DATE, 1)
            // return that day
            Constants.TOMORROW
        } else Constants.TODAY // the time is not passed, set nextAlarmDay variable it to today


    fun isTimePassed(alarmDate: Calendar) =
        alarmDate.before(Calendar.getInstance())

    private fun day(day: Int) = when (day) {

        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        Calendar.SUNDAY -> "Sunday"
        else -> "Wrong Day"
    }

//    fun formatDate(calendar: Calendar): String {
//        val sdf = SimpleDateFormat("EEE, MMM dd HH:mm a", Locale.getDefault())
//
//        val dateAndTime = sdf.format(calendar.timeInMillis)
//        val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
//        Log.d(TAG, time)
//        return time
//    }
}