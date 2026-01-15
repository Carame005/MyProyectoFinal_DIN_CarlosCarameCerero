package com.example.myproyectofinal_din_carloscaramecerero.model

import java.time.LocalDate
import java.time.LocalTime

data class EventoCalendario(
    val id: Int,
    val titulo: String,
    val fecha: LocalDate,
    val hora: LocalTime? = null
)
