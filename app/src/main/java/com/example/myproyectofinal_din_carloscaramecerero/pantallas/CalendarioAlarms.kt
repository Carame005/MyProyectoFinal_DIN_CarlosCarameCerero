package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import java.time.LocalDateTime
import java.time.ZoneId

const val NOTIF_CHANNEL_ID = "calendar_events_channel_v2"
private const val NOTIF_CHANNEL_NAME = "Eventos del calendario"
const val NOTIF_ACTION = "com.example.myproyectofinal.SHOW_EVENT"

fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val existing = nm.getNotificationChannel(NOTIF_CHANNEL_ID)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        if (existing == null) {
            val newChannel = android.app.NotificationChannel(
                NOTIF_CHANNEL_ID,
                NOTIF_CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttrs)
                enableVibration(true)
                enableLights(true)
            }
            nm.createNotificationChannel(newChannel)
            Log.d("CalendarioAlarms", "Notification channel creado: $NOTIF_CHANNEL_ID")
        } else {
            Log.d("CalendarioAlarms", "Notification channel ya existe: ${existing.id} importance=${existing.importance}")
        }
    }
}

fun scheduleAlarm(context: Context, event: CalendarEvent) {
    if (event.time.isNullOrBlank()) return
    try {
        val dateTime = LocalDateTime.of(event.date, java.time.LocalTime.parse(event.time))
        val triggerAt = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAt <= System.currentTimeMillis()) {
            Log.d("CalendarioAlarms", "No programando alarma pasada para id=${event.id}")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("eventId", event.id)
            putExtra("title", event.title)
            putExtra("date", event.date.toString())
            putExtra("time", event.time ?: "")
            action = NOTIF_ACTION
        }
        // asegurar request code no negativo y estable
        val requestCode = event.id and 0x7fffffff
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            flags
        )

        // Evitar SecurityException: comprobar si el dispositivo permite exact alarms
        var usedExact = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
            try {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
                    usedExact = true
                } else {
                    // fallback inexacto
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending)
                }
            } catch (ex: SecurityException) {
                Log.w("CalendarioAlarms", "No se permite SCHEDULE_EXACT_ALARM, fallback a alarma inexacta: ${ex.message}")
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            }
        } else {
            // API < 31: usar exacta
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            usedExact = true
        }

        Log.d("CalendarioAlarms", "Alarm scheduled: id=${event.id} requestCode=$requestCode at=$triggerAt title=${event.title} exact=$usedExact")
    } catch (ex: Exception) {
        Log.e("CalendarioAlarms", "Error scheduling alarm for event=${event.id}", ex)
    }
}

fun cancelAlarm(context: Context, eventId: Int) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply { action = NOTIF_ACTION }
        val requestCode = eventId and 0x7fffffff
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_NO_CREATE
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            flags
        )
        if (pending != null) {
            alarmManager.cancel(pending)
            pending.cancel()
            Log.d("CalendarioAlarms", "Alarm cancelled for id=$eventId requestCode=$requestCode")
        } else {
            Log.d("CalendarioAlarms", "No existing alarm to cancel for id=$eventId requestCode=$requestCode")
        }
    } catch (ex: Exception) {
        Log.e("CalendarioAlarms", "cancelAlarm error for $eventId", ex)
    }
}

fun scheduleAlarmsForUser(context: Context, userEmail: String) {
    try {
        val events = AppRepository.loadEvents(context, userEmail)
        ensureNotificationChannel(context)
        events.forEach { scheduleAlarm(context, it) }
    } catch (ex: Exception) {
        Log.e("CalendarioAlarms", "scheduleAlarmsForUser error for $userEmail", ex)
    }
}

fun scheduleAlarmsForAllUsers(context: Context) {
    try {
        val users = AppRepository.listAllUsers(context)
        users.forEach { scheduleAlarmsForUser(context, it.email) }
    } catch (ex: Exception) {
        Log.e("CalendarioAlarms", "scheduleAlarmsForAllUsers error", ex)
    }
}
