package com.example.alarmapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.alarmapp.data.AlarmItem

private const val TAG = "diffUtil"

class AlarmDiffUtil : DiffUtil.ItemCallback<AlarmItem>() {

    override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem) =
        oldItem.alarmId == newItem.alarmId

    override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem) = oldItem == newItem

}