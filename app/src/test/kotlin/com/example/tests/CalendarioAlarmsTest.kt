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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowAlarmManager
import org.robolectric.annotation.Config
import java.time.LocalDate

/**
 * Pruebas para la lógica de programación de alarmas y canal de notificaciones.
 *
 * Objetivo:
 * - Validar que la creación del canal de notificaciones no falla.
 * - Verificar que eventos en el pasado no crean alarmas.
 * - Verificar que programar y cancelar alarmas funciona usando `ShadowAlarmManager`.
 *
 * Contexto: se ejecutan con Robolectric (JVM), por lo que las interacciones con AlarmManager
 * se inspeccionan mediante *shadows*.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CalendarioAlarmsTest {
    private lateinit var ctx: Context

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
    }

    /**
     * Verifica que la creación del canal de notificaciones no lanza excepciones.
     * Caso límite: si ya existe el canal, la función debe seguir sin fallar.
     */
    @Test
    fun ensureNotificationChannel_doesNotCrash() {
        ensureNotificationChannel(ctx)
        // no exception = ok
        assertTrue(true)
    }

    /**
     * Comprueba que no se programa una alarma para un evento con fecha anterior a la actual.
     * Entrada: `CalendarEvent` con fecha pasada.
     * Aserción: `ShadowAlarmManager.scheduledAlarms` permanece vacío.
     */
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

    /**
     * Programa una alarma para el futuro y luego la cancela; verifica con el Shadow que
     * la alarma se programó y fue eliminada.
     */
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
