package com.example.alarmapp.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
@Entity(
    tableName = "alarm_table",
    indices = [Index(value = ["hour", "minute", "alarmDay"], unique = true)]
)
data class AlarmItem(

    @PrimaryKey
    val alarmId: Long,
    var alarmLabel: String,
    var hour: Int,
    var minute: Int,
    var alarmDay: String,
    var isScheduled: Boolean = true,
    val currentTime: Long
) : Parcelable