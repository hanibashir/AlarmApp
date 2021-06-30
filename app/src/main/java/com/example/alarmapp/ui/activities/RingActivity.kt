package com.example.alarmapp.ui.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmapp.R
import com.example.alarmapp.ui.fragments.RingFragment


class RingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring)

        // we don't need an actionBar for this activity
        supportActionBar?.hide()
        // turn on screen device after alarm goes off
        turnScreenOn()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.ring_activity_fragment_container, RingFragment())
            commit()
        }
    }

    private fun turnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            this.setTurnScreenOn(true)
            this.setShowWhenLocked(true)
        } else {
            // for android api versions older than 27
            @Suppress("DEPRECATION")
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}