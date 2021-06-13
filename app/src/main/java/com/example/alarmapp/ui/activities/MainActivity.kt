package com.example.alarmapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.alarmapp.R

private const val TAG = "mainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))
    }

    // to activate back navigation from the fragment action bar arrow
    override fun onSupportNavigateUp() =
        findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
}