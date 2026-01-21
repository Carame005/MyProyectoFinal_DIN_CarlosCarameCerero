package com.example.myproyectofinal_din_carloscaramecerero.model

/**
 * Estructura para descomponer un intervalo de tiempo en años/meses/días/horas.
 * Utilizado por componentes de estadísticas/progreso para mostrar tiempo transcurrido.
 */
data class TimeBreakdown(
    val years: Long,
    val months: Long,
    val days: Long,
    val hours: Long
)
