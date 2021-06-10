package com.example.alarmapp.ui.interfaces

import android.view.MenuItem
import com.example.alarmapp.data.models.AlarmItem

interface AlarmViewsOnClickListener {
    fun onSwitchToggle(position: Int)
    fun onOptionsMenuItemClicked(position: Int, menuItem: MenuItem)
}