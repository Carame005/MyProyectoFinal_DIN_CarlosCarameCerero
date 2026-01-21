package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import kotlin.random.Random

/**
 * Indicador de estado pequeño para una tarea.
 *
 * @param status Estado de la tarea (pone color asociado).
 */
@Composable
fun StatusIndicator(status: com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus) {
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

/**
 * Card que representa una tarea. Soporta expansión para ver descripción,
 * cambio de estado y eliminación (cuando está completada).
 *
 * @param task Tarea mostrada.
 * @param modifier Modifier aplicable.
 * @param onClick Callback al pulsar la tarjeta (se alterna expansión).
 * @param onStatusChange Callback para actualizar el estado de la tarea.
 * @param onDelete Callback para eliminar la tarea.
 */
@Composable
fun TaskCard(
    task: com.example.myproyectofinal_din_carloscaramecerero.model.Task,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onStatusChange: (taskId: Int, newStatus: com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus) -> Unit = { _, _ -> },
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = task.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.status == TaskStatus.DONE) TextDecoration.LineThrough else TextDecoration.None
                )
                StatusIndicator(status = task.status)
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                            TaskStatus.DONE -> { /* sin botones */ }
                        }

                        if (task.status == TaskStatus.DONE) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { onDelete(task.id) }) {
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

/**
 * Diálogo para añadir una nueva tarea.
 */
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (com.example.myproyectofinal_din_carloscaramecerero.model.Task) -> Unit
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
