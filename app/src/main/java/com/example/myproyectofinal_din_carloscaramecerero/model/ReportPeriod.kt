package com.example.myproyectofinal_din_carloscaramecerero.model

/**
 * Períodos disponibles para generar informes.
 * Cada valor tiene una descripción legible para mostrar en la UI.
 */
enum class ReportPeriod(val desc: String) {
    LAST_WEEK("Última semana"),
    LAST_MONTH("Último mes");

    override fun toString(): String = desc
}

