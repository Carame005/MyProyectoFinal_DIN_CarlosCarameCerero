package com.example.myproyectofinal_din_carloscaramecerero.model

/**
 * Resumen cuantitativo rápido del informe, usado para representar gráficos.
 */
data class ReportSummary(
    val tasksCompleted: Int = 0,
    val tasksInProgress: Int = 0,
    val tasksPending: Int = 0,
    val eventsCount: Int = 0,
    val totalVideos: Int = 0
)

