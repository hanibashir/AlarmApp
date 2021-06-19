package com.example.alarmapp.ui.fragments

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.alarmapp.R


const val KEY_RINGTONE_PREFERENCE = "key_alarm_ringtone"

class SettingsFragment : PreferenceFragmentCompat() {

    private var launchRingManager: ActivityResultLauncher<Intent>? = null


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        Log.d("Ring Tone:", "on create")

        val ringtoneTitle =
            findPreference<Preference>(resources.getString(R.string.key_alarm_ringtone))

        launchRingManager =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data

                    val ringtone =
                        data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    ringtoneTitle?.summary = ringtone?.getQueryParameter("title")
                        ?: resources.getString(R.string.summary_choose_ringtone)

                    //save ringtone uri
                    PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
                        putString(
                            resources.getString(R.string.key_alarm_ringtone),
                            ringtone.toString()
                        )
                    }.apply()
                }
            }
    }


    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return if (preference.key == KEY_RINGTONE_PREFERENCE) {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                Settings.System.DEFAULT_NOTIFICATION_URI
            )

            launchRingManager?.launch(intent)

            true
        } else {
            super.onPreferenceTreeClick(preference)
        }
    }
}