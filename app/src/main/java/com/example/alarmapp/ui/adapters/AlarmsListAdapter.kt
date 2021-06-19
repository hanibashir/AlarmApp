package com.example.alarmapp.ui.adapters

import android.app.AlarmManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmapp.R
import com.example.alarmapp.data.models.AlarmItem
import com.example.alarmapp.ui.interfaces.AlarmViewsOnClickListener
import com.example.alarmapp.utils.AlarmDiffUtil
import com.example.alarmapp.utils.CalendarUtil
import java.util.*


const val TAG = "adapter"

class AlarmsListAdapter(
    private val context: Context,
    private val clickListener: AlarmViewsOnClickListener
) : ListAdapter<AlarmItem, AlarmsListAdapter.AlarmViewHolder>(AlarmDiffUtil()) {

    // View Holder class
    class AlarmViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // init views
        private val tvLabel: TextView = itemView.findViewById(R.id.item_alarm_label)
        private val tvDay: TextView = itemView.findViewById(R.id.item_tv_day)
        private val tvTime: TextView = itemView.findViewById(R.id.item_tv_alarm_time)
        private val switchBtn: SwitchCompat = itemView.findViewById(R.id.item_btn_switch)
        private val showItemOptions: TextView = itemView.findViewById(R.id.item_show_options_menu)
        private val tvRemainTime: TextView = itemView.findViewById(R.id.item_tv_alarm_remain_time)

        // get views from data binding
        fun bind(alarmItem: AlarmItem, clickListener: AlarmViewsOnClickListener, context: Context) {
            // format alarm time
            val alarmTime = CalendarUtil().formatCalendarTime(alarmItem.hour, alarmItem.minute)
            // set calendar time
            val alarmDate = CalendarUtil().setCalendar(alarmItem.hour, alarmItem.minute)

            val alarmDay = CalendarUtil().getAlarmDay(alarmDate)

            val remainTime = alarmDate.timeInMillis - System.currentTimeMillis()

            //val seconds = (remainTime / 1000) % 60
            val minutes = (remainTime / (1000 * 60) % 60)
            val hours = (remainTime / (1000 * 60 * 60) % 24)

            val remainTimeText =
                if (hours != 0L) "After: $hours Hours and $minutes Minutes" else "After: $minutes Minutes"

            // set item views values
            tvLabel.text = (alarmItem.alarmLabel)

            if (alarmItem.isScheduled) {
                tvDay.visibility = View.VISIBLE
                tvRemainTime.visibility = View.VISIBLE
                tvDay.text = alarmDay
                tvRemainTime.text = remainTimeText
            } else {
                tvDay.visibility = View.GONE
                tvRemainTime.visibility = View.GONE
            }
            tvTime.text = alarmTime
            switchBtn.isChecked = alarmItem.isScheduled
            // switch on change state listener
            switchBtn.setOnClickListener {
                clickListener.onSwitchToggle(adapterPosition)
            }
            // options menu onclick listener
            showItemOptions.setOnClickListener { popupMenu(it, clickListener, context) }
        }

        private fun popupMenu(
            view: View,
            clickListener: AlarmViewsOnClickListener,
            context: Context
        ) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.alarm_item_options_menu)

            //popup menu item click listener
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_alarm -> clickListener.onOptionsMenuItemClicked(
                        adapterPosition,
                        menuItem
                    )
                    R.id.edit_alarm -> clickListener.onOptionsMenuItemClicked(
                        adapterPosition,
                        menuItem
                    )
                }
                false
            }
            popupMenu.show()
        }

        companion object {
            fun from(parent: ViewGroup): AlarmViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.alarm_item,
                    parent,
                    false
                )

                //Log.d(TAG, "on create view holder")
                return AlarmViewHolder(view)
            }
        }
    }

    // on create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder =
        AlarmViewHolder.from(parent)


    // on bind view holder
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) =
        holder.bind(getItem(position), clickListener, context)


}