package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import com.example.myproyectofinal_din_carloscaramecerero.utils.AddTaskDialog
import com.example.myproyectofinal_din_carloscaramecerero.utils.TaskCard
import androidx.compose.runtime.LaunchedEffect
import kotlin.random.Random
import androidx.core.content.edit

private const val PREFS_NAME = "tasks_prefs"
private const val TASKS_KEY = "tasks_serialized"

private fun serializeTasks(tasks: List<Task>): String =
    tasks.joinToString("|||") { "${it.id}::${it.title.replace("::"," ")}::${it.description.replace("::"," ")}::${it.status.name}" }

private fun deserializeTasks(serialized: String?): List<Task> {
    if (serialized.isNullOrEmpty()) return emptyList()
    return try {
        serialized.split("|||").mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size < 4) return@mapNotNull null
            val id = parts[0].toIntOrNull() ?: return@mapNotNull null
            val title = parts[1]
            val description = parts[2]
            val status = try { TaskStatus.valueOf(parts[3]) } catch (_: Exception) { TaskStatus.PENDING }
            Task(id = id, title = title, description = description, status = status)
        }
    } catch (_: Exception) {
        emptyList()
    }
}

@Composable
fun TaskListScreen() {
    val context = LocalContext.current
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var showDialog by remember { mutableStateOf(false) }

    // Cargar tareas al componer
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        tasks = deserializeTasks(prefs.getString(TASKS_KEY, null))
    }

    // Guardar cada vez que tasks cambian
    LaunchedEffect(tasks) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(TASKS_KEY, serializeTasks(tasks)) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .padding(bottom = 96.dp) // dejar espacio para botón
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(items = tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onStatusChange = { id, newStatus ->
                            // Actualiza el estado de la tarea; los botones desaparecerán si deja de ser PENDING
                            tasks = tasks.map {
                                if (it.id == id) it.copy(status = newStatus) else it
                            }
                        },
                        onDelete = { id ->
                            tasks = tasks.filter { it.id != id }
                        }
                    )
                }
            }
        }

        // Botón circular centrado abajo con icono "+"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D47A1)) // color de contraste (azul oscuro)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onTaskAdded = {
                // asigna id único sencillo
                val newTask = it.copy(id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE))
                tasks = tasks + newTask
                showDialog = false
            }
        )
    }
}
