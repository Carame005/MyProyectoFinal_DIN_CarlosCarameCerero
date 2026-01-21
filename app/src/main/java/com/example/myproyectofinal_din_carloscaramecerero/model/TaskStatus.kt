package com.example.myproyectofinal_din_carloscaramecerero.model

/**
 * Enumeración de estados que puede tener una tarea.
 *
 * El campo `desc` contiene una descripción legible en español para la UI.
 */
enum class TaskStatus (val desc : String) {
    PENDING("Pendiente"),
    IN_PROGRESS("En Progreso"),
    DONE("Hecho")
}