package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.ui.graphics.Color

/**
 * Paleta centralizada de colores de la aplicación.
 *
 * Este archivo define constantes de color reutilizables para la UI y facilita la
 * implementación futura de modos (claro/oscuro) y del "filtro claro".
 *
 * Notas:
 * - Los valores se usan directamente en componentes (botones, tarjetas, scrims).
 * - Para cambiar el tema global considere mapear estos valores a un ColorScheme de Material.
 */

// Paleta centralizada (preparada para modo oscuro)

/** Azul suave usado para estados de selección y acentos ligeros. */
val PrimaryBlue = Color(0xFF90CAF9)   // Azul suave (selección)

/** Superficie clara de tarjetas / contenedores en modo claro. */
val SurfaceGray = Color(0xFFF2F2F2)   // Fondo claro de tarjetas

/** Color principal para iconos en estado normal/activo en surfaces claras. */
val IconGray = Color(0xFF424242)      // Iconos normales

/** Color usado para estados deshabilitados o texto secundario. */
val DisabledGray = Color(0xFF9E9E9E)  // No seleccionado

/** Color destacado del botón "añadir" usado en varias pantallas (contraste fuerte). */
val AddButtonBlue = Color(0xFF0D47A1) // color de contraste (azul oscuro)

/** Fondo global oscuro (ej. pantallas de login o modo oscuro por defecto). */
val DarkBackground = Color(0xFF121212)

// --- Nuevos colores para "filtro claro" / modo claro ---

/** Surface claro para drawer y tarjetas cuando se aplica el filtro claro. */
val LightFilterSurface = Color(0xFFFFFFFF)    // surface claro para drawer y tarjetas

/** Fondo muy claro con leve tinte azul usado para áreas amplias en filtro claro. */
val LightFilterBackground = Color(0xFFF7FAFF) // fondo muy claro con leve tinte azul

/** Scrim claro semitransparente (cuando el fondo es oscuro y se quiere aclarar). */
val LightFilterScrim = Color(0x55FFFFFF)      // scrim claro semitransparente (sobre fondo oscuro)

/** Scrim oscuro semitransparente usado para overlay cuando se oscurece el fondo. */
val DarkFilterScrim = Color(0x99000000)       // scrim oscuro (ya usado antes)

// Opcional: color de texto sobre surface claro/oscuro se maneja por MaterialTheme en general
