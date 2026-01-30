package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build

/**
 * Abstracción sobre AlarmManager para permitir inyección en tests.
 */
interface AlarmScheduler {
    fun canScheduleExactAlarms(): Boolean
    fun setExactAndAllowWhileIdle(type: Int, triggerAtMillis: Long, pending: PendingIntent)
    fun set(type: Int, triggerAtMillis: Long, pending: PendingIntent)
    fun cancel(pending: PendingIntent)
}

/** Implementación real que delega en AlarmManager. */
@Suppress("unused")
class RealAlarmScheduler(private val ctx: Context) : AlarmScheduler {
    private val alarmManager: AlarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try { alarmManager.canScheduleExactAlarms() } catch (_: Throwable) { false }
        } else true
    }

    override fun setExactAndAllowWhileIdle(type: Int, triggerAtMillis: Long, pending: PendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(type, triggerAtMillis, pending)
    }

    override fun set(type: Int, triggerAtMillis: Long, pending: PendingIntent) {
        alarmManager.set(type, triggerAtMillis, pending)
    }

    override fun cancel(pending: PendingIntent) {
        alarmManager.cancel(pending)
    }
}
