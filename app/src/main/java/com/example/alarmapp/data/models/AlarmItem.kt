package com.example.alarmapp.data.models

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.alarmapp.broadcastreceivers.AlarmReceiver
import com.example.alarmapp.utils.CalendarUtil
import com.example.alarmapp.utils.Constants
import com.example.alarmapp.utils.Constants.Companion.ACTION_ALARM_RECEIVER
import com.example.alarmapp.utils.Constants.Companion.ACTION_START_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME
import com.example.alarmapp.utils.Messages
import java.util.*

@Entity(
    tableName = "alarm_table",
    indices = [Index(value = ["hour", "minute", "alarmDay"], unique = true)]
)
data class AlarmItem(

    @PrimaryKey
    val alarmId: Long,
    val alarmLabel: String,
    val hour: Int,
    val minute: Int,
    var alarmDay: String,
    var isScheduled: Boolean = true,
    val currentTime: Long
)