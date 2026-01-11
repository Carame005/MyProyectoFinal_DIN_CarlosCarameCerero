package com.example.myproyectofinal_din_carloscaramecerero.model

import java.time.Instant
import kotlin.time.ExperimentalTime

data class AbstinenceRecord @OptIn(ExperimentalTime::class) constructor(
    val title: String,          // Ej: "Autolesiones"
    val startDate: Instant      // Fecha desde la que se cuenta
)
