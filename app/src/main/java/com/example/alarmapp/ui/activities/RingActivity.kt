package com.example.alarmapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.alarmapp.R
import com.example.alarmapp.services.AlarmService
import com.example.alarmapp.databinding.ActivityRingBinding
import com.example.alarmapp.utils.Constants.Companion.ALARM_LABEL
import com.example.alarmapp.utils.Constants.Companion.ALARM_TIME

private const val TAG = "ringActivity"

class RingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ring)

        val alarmName = intent.getStringExtra(ALARM_LABEL)
        val alarmTime = intent.getStringExtra(ALARM_TIME)

        //Log.d(TAG, intent.getStringExtra(ALARM_TIME) ?: "null time")

        binding.tvRingAlarmName.text = alarmName
        binding.tvRingTime.text = alarmTime

        // buttons onclick
        binding.btnRingDismiss.setOnClickListener(this)
        binding.btnRingSnooze.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnRingDismiss -> {
                // intent to stop service
                val intent = Intent(this, AlarmService::class.java)
                stopService(intent)
                // intent to navigate to main activity
                val startMain = Intent(this, MainActivity::class.java)
                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(startMain)
                // destroy this activity
                finish()
            }
        }
    }
}