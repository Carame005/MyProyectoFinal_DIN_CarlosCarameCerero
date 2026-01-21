package com.example.myproyectofinal_din_carloscaramecerero.model

/**
 * Rutas del NavHost usadas en la aplicación.
 * Mantener las rutas aquí evita hardcodes por la app.
 */
sealed class AppRoute(val route: String) {
    object Home : AppRoute("home")
    object Tasks : AppRoute("tasks")
    object Stats : AppRoute("stats")
    object Settings : AppRoute("settings")
    object Login : AppRoute("login")
    object Calendar : AppRoute("calendar")
}
