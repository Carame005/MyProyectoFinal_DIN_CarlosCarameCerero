package com.example.myproyectofinal_din_carloscaramecerero.model

import java.time.LocalDate

/**
 * Representa un evento del calendario.
 *
 * @property id Identificador único del evento (usado para programar/cancelar alarmas).
 * @property date Fecha del evento (solo fecha, sin hora) en formato LocalDate.
 * @property title Título descriptivo del evento.
 * @property time Hora opcional en formato "HH:mm". Si es null, el evento no dispara notificación automática.
 */
data class CalendarEvent(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val time: String? // formato "HH:mm" opcional
)

/**
 * Contenedor simple para facilitar serialización/deserialización masiva.
 */
data class CalendarData(
    val events: List<CalendarEvent> = emptyList()
)

// Helper pequeño para nombres de SharedPreferences por usuario (preparación para persistencia por usuario)
object CalendarPrefs {
    private const val BASE_PREFS = "calendar_events_prefs"
    const val EVENTS_KEY = "events_serialized"
    fun prefsNameForUser(userEmail: String) = "${BASE_PREFS}_${userEmail}"
}