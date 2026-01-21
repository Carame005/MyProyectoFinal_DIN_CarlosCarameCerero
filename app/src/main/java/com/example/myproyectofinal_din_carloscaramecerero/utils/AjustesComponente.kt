package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import androidx.compose.ui.text.input.PasswordVisualTransformation

/**
 * Pantalla de ajustes de la aplicación.
 * Mas que una pantala será un menú lateral que se desplegará desde la derecha al pulsar el icono de
 * ajustes ubicado en el topbar.
 *
 * Tendrá un pequeño boton en la parte superior izuierda paea cerrarlo
 *
 * Muestra cosas como:
 *
 * Cambiar tema (claro/oscuro)
 * Configuración de notificaciones
 * Cambiar nombre de usuario
 * Cambiar contraseña (pedira la actual y la nueva)
 * Si es tutor aparecerá la opción de agregar/eliminar tutorizados (esto aun está en proceso por lo que se ommitira, se puede agregar el boton pero no la funcionalidad)
 * Guia de uso de la aplicación detallada
 * Cerrar sesión
 *
 */
// mantenemos un stub simple por compatibilidad cuando se navega directamente a SettingsScreen
@Composable
fun SettingsScreen(){
    Text("Ajustes")
}

/**
 * Drawer lateral de ajustes que se presenta como overlay encima del resto de la UI.
 *
 * Respeta la safe drawing area y permite:
 *  - cambiar nombre y contraseña,
 *  - alternar filtro claro,
 *  - alternar notificaciones,
 *  - mostrar guía y opciones de tutor (si el usuario es admin),
 *  - confirmar cierre de sesión.
 *
 * @param user Usuario actual (se usa para mostrar info y condicionar opciones de admin).
 * @param onClose Cierra el drawer (invocado desde el botón de flecha).
 * @param onChangeName Callback para actualizar el nombre del usuario.
 * @param onChangePassword Callback para cambiar contraseña (recibe old/new).
 * @param isLightTheme Estado del filtro claro.
 * @param onToggleTheme Callback que invierte el filtro claro.
 * @param notificationsEnabled Estado de notificaciones.
 * @param onToggleNotifications Callback para cambiar notificaciones.
 * @param onLogout Callback a ejecutar cuando el usuario confirma el cierre de sesión.
 */
@Composable
fun SettingsDrawer(
    user: User,
    onClose: () -> Unit,
    onChangeName: (String) -> Unit,
    onChangePassword: (oldPass: String, newPass: String) -> Unit,
    isLightTheme: Boolean, // ahora refleja el filtro claro activado
    onToggleTheme: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    // estados locales para los formularios
    var name by remember { mutableStateOf(user.name) }
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var showGuide by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) } // <-- nuevo estado para confirmación
    var showTutError by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    // elegir colores según filtro claro
    val scrimColor = if (isLightTheme) LightFilterScrim else DarkFilterScrim
    val panelSurface = if (isLightTheme) LightFilterSurface else MaterialTheme.colorScheme.surface
    val panelBackground = if (isLightTheme) LightFilterBackground else Color.Transparent
    val textColor = if (isLightTheme) Color.Black else Color.Unspecified

    // scrim + panel lateral
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding() // respetar safe drawing area
            .background(scrimColor) // scrim semitransparente según tema; no cierra al pulsar fuera
    ) {
        // panel lateral alineado a la derecha
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(340.dp)
                .align(Alignment.CenterEnd)
                .safeDrawingPadding(), // aplicar aquí también para que el contenido quede en zona segura
            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
            color = panelSurface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(panelBackground)
                .padding(16.dp)
            ) {
                // cabecera: flecha arriba-izquierda para cerrar + título
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // botón flecha a la izquierda para cerrar
                    IconButton(onClick = { onClose() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Cerrar", tint = if (isLightTheme) Color.Black else LocalContentColor.current)
                    }

                    Text(
                        text = "Ajustes",
                        color = textColor,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .weight(1f)
                    )

                    // opcional: espacio para mantener simetría (quita si no se desea)
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cambiar tema (claro/oscuro) -> ahora actúa como "filtro claro"
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Filtro claro", modifier = Modifier.weight(1f), color = textColor)
                    Switch(checked = isLightTheme, onCheckedChange = { enabled ->
                        onToggleTheme(enabled)
                    })
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notificaciones
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Notificaciones", modifier = Modifier.weight(1f), color = textColor)
                    Switch(checked = notificationsEnabled, onCheckedChange = { onToggleNotifications(it) })
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Función tutor: permitir ser tutorizado
                var allowTut by remember { mutableStateOf(user.allowTutoring) }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Función tutor", modifier = Modifier.weight(1f), color = textColor)
                    Switch(checked = allowTut, onCheckedChange = { newVal ->
                        if (!newVal) {
                            // si desea desactivar, comprobar si está en alguna lista de tutorizados
                            val isTut = AppRepository.isTutorizadoByAny(ctx, user.email)
                            if (isTut) {
                                // no permitir y mostrar error
                                showTutError = true
                                return@Switch
                            }
                        }
                        // persistir cambio
                        allowTut = newVal
                        val updated = user.copy(allowTutoring = allowTut)
                        AppRepository.saveUser(ctx, updated)
                    })
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Cambiar nombre de usuario
                Text("Cambiar nombre", style = MaterialTheme.typography.labelLarge, color = textColor)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nombre") },
                    colors = if (isLightTheme)
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightFilterSurface,
                            unfocusedContainerColor = LightFilterSurface
                        )
                    else
                        OutlinedTextFieldDefaults.colors()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onChangeName(name.trim())
                    }) {
                        Text("Guardar", color = if (isLightTheme) Color.Black else LocalContentColor.current)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cambiar contraseña (sin verificación compleja aquí)
                Text("Cambiar contraseña", style = MaterialTheme.typography.labelLarge, color = textColor)
                OutlinedTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Contraseña actual") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = if (isLightTheme)
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightFilterSurface,
                            unfocusedContainerColor = LightFilterSurface
                        )
                    else
                        OutlinedTextFieldDefaults.colors()
                )

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nueva contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = if (isLightTheme)
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightFilterSurface,
                            unfocusedContainerColor = LightFilterSurface
                        )
                    else
                        OutlinedTextFieldDefaults.colors()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        if (newPass.isNotBlank()) {
                            onChangePassword(oldPass, newPass)
                            oldPass = ""
                            newPass = ""
                        }
                    }) {
                        Text("Cambiar", color = if (isLightTheme) Color.Black else LocalContentColor.current)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Guía detallada (diálogo)
                TextButton(onClick = { showGuide = true }) {
                    Text("Guía de uso detallada", color = if (isLightTheme) Color.Black else LocalContentColor.current)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón cerrar sesión
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    TextButton(onClick = {
                        showLogoutConfirm = true // abrir diálogo de confirmación
                    }) {
                        Text("Cerrar sesión", color = Color.Red)
                    }
                }
            }
        }

        // diálogo de guía si se solicita
        if (showGuide) {
            AlertDialog(
                onDismissRequest = { showGuide = false },
                confirmButton = {
                    TextButton(onClick = { showGuide = false }) { Text("Cerrar") }
                },
                title = { Text("Guía de uso") },
                text = {
                    Column {
                        Text("Aquí encontrará una guía paso a paso de la aplicación.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("- Inicio: resumen y acceso rápido a tareas/eventos/colecciones.")
                        Text("- Tareas: gestionar tareas diarias.")
                        Text("- Calendario: añadir eventos y programar recordatorios.")
                        Text("- Progreso: colecciones de vídeos para facilitar recuerdos.")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Use el botón '+' en cada pantalla para añadir contenido. Puede cambiar su nombre y contraseña desde este panel.")
                    }
                }
            )
        }

        // Diálogo de confirmación de cierre de sesión
        if (showLogoutConfirm) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirm = false },
                title = { Text("Confirmar cierre de sesión") },
                text = { Text("¿Desea cerrar sesión y volver a la pantalla de inicio de sesión?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    }) { Text("Sí") }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancelar") }
                }
            )
        }

        // Mensaje de error al deshabilitar función tutor si está tutorizado
        if (showTutError) {
            AlertDialog(
                onDismissRequest = { showTutError = false },
                title = { Text("Error") },
                text = { Text("No puede deshabilitar la función tutor mientras está tutorizado por alguien.") },
                confirmButton = {
                    TextButton(onClick = { showTutError = false }) { Text("Aceptar") }
                }
            )
        }
    }
}
