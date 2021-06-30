package com.example.alarmapp.ui.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmapp.R
import com.example.alarmapp.data.AlarmItem
import com.example.alarmapp.ui.interfaces.AlarmViewsOnClickListener
import com.example.alarmapp.utils.AlarmDiffUtil
import com.example.alarmapp.utils.CalendarUtil
import com.google.android.material.switchmaterial.SwitchMaterial


const val TAG = "adapter"

class AlarmsListAdapter(
    private val context: Context,
    private val clickListener: AlarmViewsOnClickListener
) : ListAdapter<AlarmItem, AlarmItemViewHolder>(AlarmDiffUtil()) {

    // on create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmItemViewHolder =
        AlarmItemViewHolder.from(parent)

    // on bind view holder
    override fun onBindViewHolder(holder: AlarmItemViewHolder, position: Int) =
        holder.bind(getItem(position), clickListener, context)

}


