package com.example.alarmapp.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.alarmapp.R
import com.example.alarmapp.data.database.AlarmDatabase
import com.example.alarmapp.data.models.AlarmItem
import com.example.alarmapp.data.repository.AlarmRepository
import com.example.alarmapp.databinding.FragmentSetAlarmBinding
import com.example.alarmapp.ui.AlarmViewModelFactory
import com.example.alarmapp.ui.viewmodels.AlarmViewModel
import com.example.alarmapp.utils.AlarmHelper
import com.example.alarmapp.utils.CalendarUtil
import com.example.alarmapp.utils.TimePickerUtil
import java.util.*
import kotlin.random.Random

private const val TAG = "setAlarmFragment"

class SetAlarmFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSetAlarmBinding
    private lateinit var viewModel: AlarmViewModel
    private val args: SetAlarmFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_alarm, container, false)

        // if is edit case get arguments and set the values to views
        if (args.alarmItem != null) {
            binding.etAlarmLabel.setText(args.alarmItem?.alarmLabel)
            TimePickerUtil.setTime(binding.timePicker, args.alarmItem?.hour, args.alarmItem?.minute)
        }

        // get a reference to the application context. we need application context to
        // create database instance
        val application = requireNotNull(this.activity).application
        // create database and get reference to Dao Object
        val dataDao = AlarmDatabase.getDatabaseInstance(application).alarmDao()
        // get reference to the repository class
        val repository = AlarmRepository(dataDao)
        //get instance of the viewModelFactory
        val viewModelFactory = AlarmViewModelFactory(repository)
        // initialize the ViewModel class
        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmViewModel::class.java)


        // if the buttons are clicked use the implemented (View.OnClickListener) interface
        binding.btnSetAlarm.setOnClickListener(this)
        binding.btnCancelAndClose.setOnClickListener(this)
        binding.cbRepeat.setOnClickListener(this)

        // return the root element view of the associated xml layout
        return binding.root
    }


    override fun onClick(v: View?) {

        when (v) {
            // if is repeat alarm
            binding.cbRepeat -> setRepeat(binding.cbRepeat.isChecked)
            // if btn set alarm clicked
            binding.btnSetAlarm -> setAlarm(args.alarmItem)
            // if btn cancel clicked
            binding.btnCancelAndClose -> cancelAndClose()
        }
    }


    private fun setRepeat(isChecked: Boolean) {
        // change days layout visibility
        if (isChecked)
            binding.repeatDaysLinearLayout.visibility = View.VISIBLE
        else
            binding.repeatDaysLinearLayout.visibility = View.GONE
    }

    private fun setAlarm(alarmItem: AlarmItem?) {
        val alarmHelper = AlarmHelper(requireContext())
        // alarm title
        var label = binding.etAlarmLabel.text.toString()
        if (label.isBlank()) label = "No Label"
        // get time picker time
        val (hour, minute) = TimePickerUtil.getTime(binding.timePicker)
        // format alarm time
        val alarmTimeString = CalendarUtil.formatCalendarTime(hour, minute)
        // set calendar time
        val alarmDate = CalendarUtil.setCalendar(hour, minute, 0)
        // if alarm time is passed add one day
        if (alarmDate.before(Calendar.getInstance())) alarmDate.add(Calendar.DATE, 1)
        // get the alarm day
        val alarmDay = CalendarUtil.getAlarmDay(alarmDate)
        // set random number for the alarm id field in database

        if (alarmItem == null) insertAlarm(label, hour, minute, alarmDay, alarmHelper)
        else updateAlarm(alarmItem, label, hour, minute, alarmDay, alarmHelper)


        // navigate back to the main fragment
        findNavController().navigate(R.id.action_setAlarmFragment_to_mainFragment)

    }

    private fun insertAlarm(
        label: String,
        hour: Int,
        minute: Int,
        alarmDay: String,
        alarmHelper: AlarmHelper
    ) {
        val alarmId = Random.nextInt(Int.MAX_VALUE).toLong()

        val alarmItem = AlarmItem(
            alarmId,
            label,
            hour,
            minute,
            alarmDay,
            true,
            System.currentTimeMillis()
        )

        // schedule alarm
        alarmHelper.scheduleAlarm(alarmItem)

        // pass the alarm object to view model
        viewModel.insertAlarm(alarmItem)
    }

    private fun updateAlarm(
        alarmItem: AlarmItem, label: String,
        hour: Int,
        minute: Int,
        alarmDay: String,
        alarmHelper: AlarmHelper
    ) {
        // cancel old alarm
        alarmHelper.cancelAlarm(alarmItem)

        alarmItem.alarmLabel = label
        alarmItem.hour = hour
        alarmItem.minute = minute
        alarmItem.alarmDay = alarmDay

        // schedule alarm
        alarmHelper.scheduleAlarm(alarmItem)

        // update alarm item
        viewModel.updateAlarm(alarmItem)
    }


    // if cancel button clicked, navigate back to the main fragment and
    // we need to set pop to and inclusive in the navigation graph
    private fun cancelAndClose() =
        findNavController().navigate(R.id.action_setAlarmFragment_to_mainFragment)
}