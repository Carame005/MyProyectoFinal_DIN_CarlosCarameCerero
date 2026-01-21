package com.example.myproyectofinal_din_carloscaramecerero.model

/**
 * Modelo de tarea simple.
 *
 * @property id Identificador único.
 * @property title Título de la tarea.
 * @property description Descripción detallada.
 * @property status Estado actual de la tarea (Pendiente/En progreso/Hecho).
 * @property createdByTutor Indica si la tarea fue creada por un tutor (impide eliminación por el tutorizado).
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val createdByTutor: Boolean = false
)