package com.example.tests

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.scheduleAlarm
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.cancelAlarm
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.ensureNotificationChannel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlarmManager
import java.time.LocalDate

class CalendarioAlarmsTest {
    private lateinit var ctx: Context

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun ensureNotificationChannel_doesNotCrash() {
        ensureNotificationChannel(ctx)
        // no exception = ok
        assertTrue(true)
    }

    @Test
    fun scheduleAlarm_past_event_is_not_scheduled() {
        val past = CalendarEvent(id = 100, date = LocalDate.now().minusDays(1), title = "Past", time = "12:00")
        scheduleAlarm(ctx, past)
        // comprobar que no hay alarm set for that requestCode
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadow = Shadows.shadowOf(am) as ShadowAlarmManager
        val scheduled = shadow.scheduledAlarms
        assertTrue(scheduled.isEmpty())
    }

    @Test
    fun scheduleAndCancel_alarm_is_scheduled_then_cancelled() {
        val future = CalendarEvent(id = 101, date = LocalDate.now().plusDays(1), title = "Future", time = "12:00")
        scheduleAlarm(ctx, future)
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadow = Shadows.shadowOf(am) as ShadowAlarmManager
        val scheduled = shadow.scheduledAlarms
        assertTrue(scheduled.isNotEmpty())
        // now cancel
        cancelAlarm(ctx, 101)
        val after = shadow.scheduledAlarms
        // in Robolectric cancellation removes it
        assertTrue(after.isEmpty())
    }
}

