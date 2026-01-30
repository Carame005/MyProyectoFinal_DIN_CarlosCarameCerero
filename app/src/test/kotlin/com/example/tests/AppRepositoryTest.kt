package com.example.tests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

/**
 * Pruebas unitarias para `AppRepository`.
 *
 * Objetivo:
 * - Verificar que las operaciones de persistencia (guardar/cargar) funcionan correctamente
 *   para usuarios, tareas, eventos y la lista de tutorizados.
 *
 * Contrato:
 * - Entrada: `Context` de prueba (Robolectric) y objetos de dominio (`User`, `Task`, `CalendarEvent`).
 * - Salida/efecto: ficheros JSON en el almacenamiento privado del `Context` y valores devueltos
 *   por las funciones `load*`.
 * - Error modes: las funciones deben devolver `null` o listas vacías cuando no existe fichero
 *   y no deben lanzar excepciones en condiciones esperadas.
 *
 * Casos cubiertos:
 * - Roundtrip guardar / cargar para usuario, tareas y eventos.
 * - Limpieza global de datos (`clearAllData`).
 * - Gestión de tutorizados (añadir / eliminar / listar).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AppRepositoryTest {
    private lateinit var ctx: Context

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
        // limpiar datos previos
        AppRepository.clearAllData(ctx)
    }

    /**
     * Guarda un `User` y verifica que se recupera correctamente.
     * Entrada: objeto `User`.
     * Aserción: `loadUser` devuelve el mismo `name` y `email`.
     */
    @Test
    fun saveAndLoadUser_roundtrip() {
        val u = User(name = "Prueba", email = "prueba@example.com", avatarRes = 0, esAdmin = false)
        AppRepository.saveUser(ctx, u)
        val loaded = AppRepository.loadUser(ctx, "prueba@example.com")
        assertNotNull(loaded)
        assertEquals("Prueba", loaded?.name)
        assertEquals("prueba@example.com", loaded?.email)
    }

    /**
     * Guarda una lista de tareas y comprueba que se leen correctamente.
     * Caso límite: lista vacía si no existe fichero.
     */
    @Test
    fun saveAndLoadTasks_roundtrip() {
        val t1 = Task(id = 1, title = "T1", description = "d1", status = TaskStatus.PENDING, createdByTutor = true)
        val t2 = Task(id = 2, title = "T2", description = "d2", status = TaskStatus.DONE, createdByTutor = false)
        AppRepository.saveTasks(ctx, "u@example.com", listOf(t1, t2))
        val loaded = AppRepository.loadTasks(ctx, "u@example.com")
        assertEquals(2, loaded.size)
        assertTrue(loaded.any { it.id == 1 && it.title == "T1" })
        assertTrue(loaded.any { it.id == 2 && it.status == TaskStatus.DONE })
    }

    /**
     * Guarda eventos y verifica la recuperación, incluyendo eventos sin hora (`time == null`).
     */
    @Test
    fun saveAndLoadEvents_roundtrip() {
        val e1 = CalendarEvent(id = 10, date = LocalDate.now().plusDays(1), title = "E1", time = "12:00", createdByTutor = true)
        val e2 = CalendarEvent(id = 11, date = LocalDate.now().plusDays(2), title = "E2", time = null, createdByTutor = false)
        AppRepository.saveEvents(ctx, "u2@example.com", listOf(e1, e2))
        val loaded = AppRepository.loadEvents(ctx, "u2@example.com")
        assertEquals(2, loaded.size)
        assertTrue(loaded.any { it.id == 10 && it.title == "E1" })
        assertTrue(loaded.any { it.id == 11 && it.time == null })
    }

    /**
     * Comprueba que `clearAllData` elimina los ficheros asociados al usuario.
     */
    @Test
    fun clearAllData_removesFiles() {
        val u = User(name = "Prueba2", email = "prueba2@example.com", avatarRes = 0, esAdmin = false)
        AppRepository.saveUser(ctx, u)
        AppRepository.saveTasks(ctx, u.email, listOf())
        AppRepository.saveEvents(ctx, u.email, listOf())
        // ensure files exist
        assertNotNull(AppRepository.loadUser(ctx, u.email))
        AppRepository.clearAllData(ctx)
        assertNull(AppRepository.loadUser(ctx, u.email))
    }

    /**
     * Test de gestión de tutorizados: añade y elimina y comprueba la lista resultante.
     */
    @Test
    fun tutorizados_management() {
        // crear tutor y tutorizado
        val tutorEmail = "tutor@example.com"
        val tuteEmail = "tute@example.com"
        AppRepository.saveTutorizados(ctx, tutorEmail, listOf())
        AppRepository.addTutorizado(ctx, tutorEmail, tuteEmail)
        val list = AppRepository.loadTutorizados(ctx, tutorEmail)
        assertTrue(list.contains(tuteEmail))
        AppRepository.removeTutorizado(ctx, tutorEmail, tuteEmail)
        val after = AppRepository.loadTutorizados(ctx, tutorEmail)
        assertFalse(after.contains(tuteEmail))
    }
}
