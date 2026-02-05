package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.utils.AddTaskDialog
import com.example.myproyectofinal_din_carloscaramecerero.utils.TaskCard
import androidx.compose.runtime.LaunchedEffect
import kotlin.random.Random
import com.example.myproyectofinal_din_carloscaramecerero.model.User

/**
 * Pantalla de lista de tareas para el usuario identificado por [userEmail].
 *
 * Carga y guarda tareas mediante [AppRepository] por perfil. Muestra la lista y
 * un botón flotante para añadir nuevas tareas (abre [AddTaskDialog]).
 *
 * @param userEmail Email del usuario actual (clave para la persistencia por perfil).
 */
@Composable
fun TaskListScreen(userEmail: String) {
    val context = LocalContext.current
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var showDialog by remember { mutableStateOf(false) }

    // Cargar tareas al componer (desde AppRepository por usuario)
    LaunchedEffect(userEmail) {
        tasks = AppRepository.loadTasks(context, userEmail)
    }

    // Guardar cada vez que tasks cambian
    LaunchedEffect(tasks) {
        AppRepository.saveTasks(context, userEmail, tasks)
    }

    // cargar usuario actual para comprobar rol
    var currentUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(userEmail) {
        currentUser = AppRepository.loadUser(context, userEmail)
    }

    val isAdmin = currentUser?.esAdmin == true

    // comprobar si este usuario está marcado como tutorizado por algún tutor; si lo está, no podrá eliminar items creados por tutor
    val isTutorizado = AppRepository.isTutorizadoByAny(context, userEmail)

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
                            // sólo permitir eliminar si el usuario es admin y (la tarea no fue creada por tutor o el usuario no es tutorizado)
                            if (isAdmin && (!task.createdByTutor || !isTutorizado)) {
                                tasks = tasks.filter { it.id != id }
                            }
                        },
                        canDelete = isAdmin && !(isTutorizado && task.createdByTutor)
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
