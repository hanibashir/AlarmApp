package com.example.alarmapp.utils

import android.content.Context

object DialogHelper {

    fun showDialog(context: Context, dialogInterface: DialogInterface) {
        android.app.AlertDialog.Builder(context)
            .setTitle("Delete")
            .setMessage("Are you sure you want delete all alarms?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                dialogInterface.getRespond(1)
            }
            .setNegativeButton("No") { dialog, _ ->
                // else dismiss the dialog
                dialogInterface.getRespond(0)
                dialog.dismiss()
            }
            .create()
            .show()
    }

    interface DialogInterface {
        fun getRespond(respond: Int)
    }
}