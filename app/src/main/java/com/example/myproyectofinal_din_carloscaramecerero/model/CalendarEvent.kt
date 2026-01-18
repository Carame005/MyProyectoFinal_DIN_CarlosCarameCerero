package com.example.myproyectofinal_din_carloscaramecerero.model

import java.time.LocalDate

// Data class que representa un evento de calendario.
// Usamos LocalDate para mantener compatibilidad con la lógica actual en CalendarioPantalla.kt
data class CalendarEvent(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val time: String? // formato "HH:mm" opcional
)

// Contenedor/DTO para futuras operaciones de persistencia o sincronización
data class CalendarData(
    val events: List<CalendarEvent> = emptyList()
)

// Helper pequeño para nombres de SharedPreferences por usuario (preparación para persistencia por usuario)
object CalendarPrefs {
    private const val BASE_PREFS = "calendar_events_prefs"
    const val EVENTS_KEY = "events_serialized"
    fun prefsNameForUser(userEmail: String) = "${BASE_PREFS}_${userEmail}"
}