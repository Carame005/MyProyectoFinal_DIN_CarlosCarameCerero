package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.net.Uri
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem

/**
 * Top bar principal con avatar, ajustes y botón de ayuda contextual.
 *
 * @param user Usuario actual.
 * @param onSettingsClick Callback cuando se pulsa el icono de ajustes.
 * @param onAvatarChange Callback para cambiar avatar.
 * @param currentRoute Ruta actual (opcional) para mostrar la mini-guía contextual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    user: com.example.myproyectofinal_din_carloscaramecerero.model.User,
    onSettingsClick: () -> Unit,
    onAvatarChange: (Uri) -> Unit,
    currentRoute: String? = null // <-- nuevo parámetro opcional para saber en qué pantalla estamos
) {
    var profileExpanded by remember { mutableStateOf(false) }
    var helpExpanded by remember { mutableStateOf(false) } // estado para el diálogo de ayuda

    Box {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceGray
            ),
            navigationIcon = {
                IconButton(onClick = { profileExpanded = true }) {
                    // ...existing avatar rendering...
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    ) {
                        if (user.avatarUri != null) {
                            AsyncImage(
                                model = user.avatarUri,
                                contentDescription = "Perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(user.avatarRes),
                                contentDescription = "Perfil",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = IconGray
                    )
                }

                // Nuevo botón de ayuda (info) junto a settings
                IconButton(onClick = { helpExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Ayuda",
                        tint = IconGray
                    )
                }
            }
        )

        ProfileMenu(
            user = user,
            expanded = profileExpanded,
            onDismiss = { profileExpanded = false },
            onAvatarChange = onAvatarChange
        )
    }

    // Dialogo de ayuda/mini guía contextual según currentRoute
    if (helpExpanded) {
        val helpText = when (currentRoute) {
            "tasks" , "Tareas", "Tareas" -> "Esta pantalla muestra sus tareas. Puede marcar como completadas o eliminar tareas. Use el botón '+' para añadir una nueva tarea."
            "calendar", "Calendar", "Calendario" -> "Calendario mensual: pulse un día para ver o añadir eventos. Puede fijar una hora para recibir un recordatorio."
            "home", "Home", "Inicio" -> "Pantalla inicial: resumen rápido de tareas, eventos y colecciones. Toque una tarjeta para expandir más detalles."
            "stats", "Stats", "Progreso" -> "Colecciones de vídeo: agrupe vídeos para recordar momentos. Pulse una colección para ver y reproducir los vídeos."
            else -> "Esta es una mini guía: toque elementos en pantalla para ver más opciones. Use el botón '+' para añadir contenido donde proceda."
        }

        AlertDialog(
            onDismissRequest = { helpExpanded = false },
            confirmButton = {
                TextButton(onClick = { helpExpanded = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Ayuda rápida") },
            text = { Text(helpText) }
        )
    }
}

/**
 * Ítem de la barra inferior.
 */
@Composable
fun BottomBarItem(
    item: com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) PrimaryBlue else SurfaceGray
    val iconColor = if (selected) Color.White else IconGray

    Box(
        modifier = Modifier
            .padding(6.dp)
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconColor
        )
    }
}

/**
 * Barra inferior con los items proporcionados.
 *
 * @param items Lista de BottomNavItem.
 * @param currentRoute Ruta activa para marcar el item seleccionado.
 * @param onItemSelected Callback cuando se selecciona un item.
 */
@Composable
fun AppBottomBar(
    items: List<com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem>,
    currentRoute: String,
    onItemSelected: (com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            BottomBarItem(
                item = item,
                selected = item.route == currentRoute,
                onClick = { onItemSelected(item) }
            )
        }
    }
}
