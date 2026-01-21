package com.example.myproyectofinal_din_carloscaramecerero.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Elemento para la barra de navegación inferior.
 *
 * @property route Ruta de navegación asociada (concuerda con AppRoute).
 * @property icon Icono a mostrar.
 * @property label Texto de la etiqueta.
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)
