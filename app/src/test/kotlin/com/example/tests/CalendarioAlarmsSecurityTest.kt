package com.example.tests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.scheduleAlarm
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.AlarmScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

/**
 * Prueba que verifica el manejo de `SecurityException` al intentar usar alarmas exactas.
 *
 * Objetivo:
 * - Comprobar que cuando `setExactAndAllowWhileIdle` lanza `SecurityException`, el sistema
 *   realiza el fallback a `set(...)`.
 *
 * Enfoque: se inyecta un `FakeScheduler` que registra llamadas y lanza la excepción en la ruta exacta.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CalendarioAlarmsSecurityTest {
    private lateinit var ctx: Context
    private lateinit var called: MutableList<String>

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
        called = mutableListOf()
    }

    /**
     * Fake que simula el comportamiento de AlarmScheduler en tests:
     * - `setExactAndAllowWhileIdle` registra y lanza `SecurityException`.
     * - `set` registra la llamada de fallback.
     */
    class FakeScheduler(val called: MutableList<String>) : AlarmScheduler {
        override fun canScheduleExactAlarms(): Boolean = true
        override fun setExactAndAllowWhileIdle(type: Int, triggerAtMillis: Long, pending: android.app.PendingIntent) {
            called.add("exact")
            throw SecurityException("Simulated deny exact alarm")
        }
        override fun set(type: Int, triggerAtMillis: Long, pending: android.app.PendingIntent) {
            called.add("set")
        }
        override fun cancel(pending: android.app.PendingIntent) {
            called.add("cancel")
        }
    }

    /**
     * Ejecuta `scheduleAlarm` con el `FakeScheduler` y comprueba que se intentó la llamada
     * exacta (que falló) y luego la llamada `set` (fallback).
     */
    @Test
    fun scheduleAlarm_fallbacks_to_set_on_security_exception() {
        val event = CalendarEvent(id = 5000, date = LocalDate.now().plusDays(1), title = "E", time = "12:00", createdByTutor = false)
        val fake = FakeScheduler(called)
        // call scheduleAlarm with fake scheduler
        scheduleAlarm(ctx, event, fake)
        // after call, should have attempted exact then fallback to set
        assertTrue(called.contains("exact"))
        assertTrue(called.contains("set"))
    }
}
