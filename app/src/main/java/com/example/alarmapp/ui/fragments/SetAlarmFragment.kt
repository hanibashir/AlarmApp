package com.example.alarmapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.alarmapp.R
import com.example.alarmapp.data.models.AlarmItem
import com.example.alarmapp.databinding.FragmentSetAlarmBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_alarm, container, false)

        // initialize the ViewModel class
        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)


        // if the buttons are clicked use the implemented (View.OnClickListener) interface
        binding.btnSetNewAlarm.setOnClickListener(this)
        binding.btnCancelAndClose.setOnClickListener(this)

        // return the root element view of the associated xml layout
        return binding.root
    }

    override fun onClick(v: View?) {

        when (v) {
            // if btn set alarm clicked
            binding.btnSetNewAlarm -> setAlarm()
            // if btn cancel clicked
            binding.btnCancelAndClose -> cancelAndClose()
        }
    }

    private fun setAlarm() {
        val alarmHelper = AlarmHelper(requireContext())
        // alarm title
        val label = binding.setAlarmLabel.text.toString()
        // get time picker time
        val (hour, minute) = TimePickerUtil.getTime(binding.itemTimePicker)
        // format alarm time
        val alarmTimeString = CalendarUtil.formatCalendarTime(hour, minute)
        // set calendar time
        val alarmDate = CalendarUtil.setCalendar(hour, minute, 0)
        // if alarm time is passed add one day
        if (alarmDate.before(Calendar.getInstance())) alarmDate.add(Calendar.DATE, 1)
        // get the alarm day
        val alarmDay = CalendarUtil.getAlarmDay(alarmDate)
        // set random number for the alarm id field in database

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

        // navigate back to the main fragment
        findNavController().navigate(R.id.action_setAlarmFragment_to_mainFragment)

    }

    // if cancel button clicked, navigate back to the main fragment and
    // we need to set pop to and inclusive in the navigation graph
    private fun cancelAndClose() =
        findNavController().navigate(R.id.action_setAlarmFragment_to_mainFragment)
}