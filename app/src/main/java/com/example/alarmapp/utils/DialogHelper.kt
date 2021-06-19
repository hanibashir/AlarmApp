package com.example.alarmapp.utils

import android.content.Context
import com.example.alarmapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogHelper {

    fun showDialog(context: Context, dialogInterface: DialogInterface) {
        MaterialAlertDialogBuilder(context, R.style.AlertDialog)
            .setTitle("Delete")
            .setMessage("Are you sure you want delete all alarms?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                dialogInterface.onRespond(1)
            }
            .setNegativeButton("No") { dialog, _ ->
                // else dismiss the dialog
                dialogInterface.onRespond(0)
                dialog.dismiss()
            }
            .create()
            .show()
    }

    interface DialogInterface {
        fun onRespond(respond: Int)
    }
}