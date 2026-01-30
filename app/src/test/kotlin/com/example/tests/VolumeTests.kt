package com.example.tests

import androidx.test.core.app.ApplicationProvider
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

/**
 * Tests de volumen básicos para evaluar la persistencia local y medir tiempos/tamaños.
 *
 * Objetivo:
 * - Validar que `AppRepository` puede almacenar y recuperar un número moderado de elementos
 *   (500 en estos tests) y registrar métricas simples (tiempo de escritura y tamaño del fichero).
 *
 * Consideraciones:
 * - Estos tests escriben en el filesystem simulado por Robolectric; en CI se puede reducir
 *   la carga o usar una estrategia alternativa para no ralentizar la canalización.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class VolumeTests {
    private val userEmail = "volumen@example.com"

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        AppRepository.clearAllData(ctx)
    }

    /**
     * Genera `n` eventos, los guarda y verifica que se recuperan todos; además mide tiempo y tamaño del fichero.
     * Medidas impresas en consola para captura manual/CI.
     */
    @Test
    fun saveManyEvents_and_measure_size() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val n = 500
        val events = (1..n).map { i ->
            CalendarEvent(id = i, date = LocalDate.now().plusDays(i.toLong()), title = "E$i", time = "12:00")
        }
        val start = System.currentTimeMillis()
        AppRepository.saveEvents(ctx, userEmail, events)
        val elapsed = System.currentTimeMillis() - start
        val loaded = AppRepository.loadEvents(ctx, userEmail)
        assertTrue(loaded.size == n)
        // medir tamaño aproximado del fichero
        val fn = ctx.getFileStreamPath(userEmail.replace(Regex("[^A-Za-z0-9_]"), "_") + "_events.json")
        val size = if (fn.exists()) fn.length() else 0L
        println("Saved $n events in ${elapsed}ms, file size=${size} bytes")
        assertTrue(size > 0)
    }

    /**
     * Genera `n` tareas, las guarda y verifica que se recuperan todas; mide tiempo y tamaño.
     */
    @Test
    fun saveManyTasks_and_measure_size() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val n = 500
        val tasks = (1..n).map { i -> Task(id = i, title = "T$i", description = "d$i", status = TaskStatus.PENDING) }
        val start = System.currentTimeMillis()
        AppRepository.saveTasks(ctx, userEmail, tasks)
        val elapsed = System.currentTimeMillis() - start
        val loaded = AppRepository.loadTasks(ctx, userEmail)
        assertTrue(loaded.size == n)
        val fn = ctx.getFileStreamPath(userEmail.replace(Regex("[^A-Za-z0-9_]"), "_") + "_tasks.json")
        val size = if (fn.exists()) fn.length() else 0L
        println("Saved $n tasks in ${elapsed}ms, file size=${size} bytes")
        assertTrue(size > 0)
    }
}
