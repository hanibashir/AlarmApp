package com.example.alarmapp.ui.activities

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alarmapp.R
import com.example.alarmapp.databinding.ActivityRingBinding
import com.example.alarmapp.services.AlarmService
import com.example.alarmapp.utils.Constants
import com.example.alarmapp.utils.Constants.Companion.ACTION_STOP_SERVICE
import com.example.alarmapp.utils.Constants.Companion.ALARM_ID
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME


class RingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ring)

        // we don't need an actionBar for this activity
        supportActionBar?.hide()

        // init activity views
        initViews()




        // set screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }

    private fun initViews() {
        // get intent extras
        val (alarmId, alarmLabel, alarmTime) = getIntentExtra()
        binding.tvRingAlarmLabel.text = alarmLabel
        binding.tvRingAlarmTime.text = alarmTime
    }

    private fun getIntentExtra() : Triple<Long?, String?, String?> {
        // get intent extra from broadcast receiver
        val alarmId = intent.getLongExtra(ALARM_ID, 0)
        val alarmLabel = intent.getStringExtra(ALARM_LABEL)
        val alarmTime = intent.getStringExtra(ALARM_TIME)
        return Triple(alarmId, alarmLabel, alarmTime)
    }

    override fun onStart() {
        super.onStart()
        // stop the foreground service
        stopService(stopServiceIntent())
    }

    private fun stopServiceIntent() = Intent(this, AlarmService::class.java).apply {
        action = ACTION_STOP_SERVICE
    }

}