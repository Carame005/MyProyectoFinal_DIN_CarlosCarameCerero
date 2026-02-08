package com.example.myproyectofinal_din_carloscaramecerero.model

import com.example.myproyectofinal_din_carloscaramecerero.model.ReportPeriod

/**
 * Filtros usados para generar informes.
 */
data class ReportFilters(
    val period: ReportPeriod = ReportPeriod.LAST_WEEK,
    val includeCompleted: Boolean = true,
    val includePending: Boolean = true,
    val includeInProgress: Boolean = true,
    val includeEvents: Boolean = true,
    val includeVideos: Boolean = true
)

