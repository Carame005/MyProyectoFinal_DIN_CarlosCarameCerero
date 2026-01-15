package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Pantalla de ajustes de la aplicación.
 * Mas que una pantala será un menú lateral que se desplegará desde la derecha al pulsar el icono de
 * ajustes ubicado en el topbar.
 *
 * Tendrá un pequeño boton en la parte superiro izuierda paea cerrarlo
 *
 * Muestra cosas como:
 *
 * Cambiar tema (claro/oscuro)
 * Configuración de notificaciones
 * Si es tutor aparecerá la opción de agregar/eliminar tutorizados
 * Cerrar sesión
 *
 */
@Composable
fun SettingsScreen(){
    Text("Ajustes")
}