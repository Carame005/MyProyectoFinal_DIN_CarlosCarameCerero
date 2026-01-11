package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.net.Uri
import android.os.Build
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myproyectofinal_din_carloscaramecerero.R
import com.example.myproyectofinal_din_carloscaramecerero.model.AbstinenceRecord
import com.example.myproyectofinal_din_carloscaramecerero.model.BottomNavItem
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import com.example.myproyectofinal_din_carloscaramecerero.model.TimeBreakdown
import java.time.Instant
import kotlin.random.Random

val PrimaryBlue = Color(0xFF90CAF9)   // Azul suave (selección)
val SurfaceGray = Color(0xFFF2F2F2)   // Fondo
val IconGray = Color(0xFF424242)      // Iconos normales
val DisabledGray = Color(0xFF9E9E9E)  // No seleccionado

// Nuevo: color del botón "añadir" usado en TareaPantalla y switch
val AddButtonBlue = Color(0xFF0D47A1) // color de contraste (azul oscuro)

// Nuevo: fondo oscuro global para pantalla de login
val DarkBackground = Color(0xFF121212)


@Composable
fun StatusIndicator(status: TaskStatus) {
    val color = when (status) {
        TaskStatus.DONE -> Color(0xFF4CAF50)       // verde
        TaskStatus.PENDING -> Color(0xFFF44336)    // rojo
        TaskStatus.IN_PROGRESS -> Color(0xFFFF9800)// naranja
    }

    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onStatusChange: (taskId: Int, newStatus: TaskStatus) -> Unit = { _, _ -> },
    onDelete: (taskId: Int) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                expanded = !expanded
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fila principal
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    // Si la tarea está hecha, aplicar tachado
                    textDecoration = if (task.status == TaskStatus.DONE) TextDecoration.LineThrough else TextDecoration.None
                )

                StatusIndicator(status = task.status)
            }

            // Descripción desplegable
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fila con comportamiento por estado:
                    // - PENDING -> solo "En progreso"
                    // - IN_PROGRESS -> solo "Hecho"
                    // - DONE -> ningún botón, aparece solo el cubo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (task.status) {
                            TaskStatus.PENDING -> {
                                Button(
                                    onClick = { onStatusChange(task.id, TaskStatus.IN_PROGRESS) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                                ) {
                                    Text("En progreso")
                                }
                            }
                            TaskStatus.IN_PROGRESS -> {
                                Button(
                                    onClick = { onStatusChange(task.id, TaskStatus.DONE) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                                ) {
                                    Text("Hecho")
                                }
                            }
                            TaskStatus.DONE -> {
                                // sin botones; el cubo aparece más abajo
                            }
                        }

                        // Mostrar el cubo SOLO si la tarea está marcada como DONE
                        if (task.status == TaskStatus.DONE) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onDelete(task.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color(0xFFB00020)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onTaskAdded(
                    Task(
                        id = Random.nextInt(),
                        title = title,
                        description = description,
                        status = TaskStatus.PENDING
                    )
                )
                onDismiss()
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Nueva tarea") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
            }
        }
    )
}

val defaultUser = User(
    name = "Usuario",
    email = "usuario@correo.com",
    avatarRes = R.drawable.pfp
)

@Composable
fun ProfileMenu(
    user: User,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onAvatarChange: (Uri) -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAvatarChange(it) }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable {
                        imagePicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
            ) {
                if (user.avatarUri != null) {
                    AsyncImage(
                        model = user.avatarUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(user.avatarRes),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user.name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    user: User,
    onSettingsClick: () -> Unit,
    onAvatarChange: (Uri) -> Unit   // 👈 se eleva el evento
) {
    var profileExpanded by remember { mutableStateOf(false) }

    Box {
        TopAppBar(
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceGray
            ),
            navigationIcon = {
                IconButton(onClick = { profileExpanded = true }) {
                    // ...reemplazado: mostrar AsyncImage si hay avatarUri, si no fallback a recurso drawable...
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
            }
        )

        ProfileMenu(
            user = user,
            expanded = profileExpanded,
            onDismiss = { profileExpanded = false },
            onAvatarChange = onAvatarChange // 👈 delega
        )
    }
}



@Composable
fun BottomBarItem(
    item: BottomNavItem,
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


@Composable
fun AppBottomBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
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

@RequiresApi(Build.VERSION_CODES.O)
val defaultRecord = AbstinenceRecord(
    title = "autolesiones",
    startDate = Instant.parse("2022-01-01T00:00:00Z")
)

@Composable
fun TimeProgressItem(
    label: String,
    value: Long,
    maxValue: Long,
    color: Color
) {
    val progress = (value.toFloat() / maxValue.coerceAtLeast(1)).coerceIn(0f, 1f)

    Column {
        Text(
            text = "$value $label",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
fun TimeProgressChart(time: TimeBreakdown) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        TimeProgressItem("años", time.years, 10, Color(0xFF2ECC71))
        TimeProgressItem("meses", time.months, 12, Color(0xFF27AE60))
        TimeProgressItem("días", time.days, 30, Color(0xFF3498DB))
        TimeProgressItem("horas", time.hours, 24, Color(0xFF5DADE2))
    }
}
