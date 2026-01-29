package com.example.myproyectofinal_din_carloscaramecerero.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.scheduleAlarmsForAllUsers

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                Log.d("BootReceiver", "BOOT_COMPLETED received - re-scheduling alarms for all users")
                scheduleAlarmsForAllUsers(context)
            }
        } catch (ex: Exception) {
            Log.e("BootReceiver", "Error in BootReceiver.onReceive", ex)
        }
    }
}

