package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.ui.graphics.Color

// Paleta centralizada (preparada para modo oscuro)
val PrimaryBlue = Color(0xFF90CAF9)   // Azul suave (selección)
val SurfaceGray = Color(0xFFF2F2F2)   // Fondo claro de tarjetas
val IconGray = Color(0xFF424242)      // Iconos normales
val DisabledGray = Color(0xFF9E9E9E)  // No seleccionado

// Color del botón "añadir" usado en TareaPantalla, Login, etc.
val AddButtonBlue = Color(0xFF0D47A1) // color de contraste (azul oscuro)

// Fondo oscuro global (ej. login)
val DarkBackground = Color(0xFF121212)

// --- Nuevos colores para "filtro claro" / modo claro ---
val LightFilterSurface = Color(0xFFFFFFFF)    // surface claro para drawer y tarjetas
val LightFilterBackground = Color(0xFFF7FAFF) // fondo muy claro con leve tinte azul
val LightFilterScrim = Color(0x55FFFFFF)      // scrim claro semitransparente (sobre fondo oscuro)
val DarkFilterScrim = Color(0x99000000)       // scrim oscuro (ya usado antes)

// Opcional: color de texto sobre surface claro/oscuro se maneja por MaterialTheme en general
