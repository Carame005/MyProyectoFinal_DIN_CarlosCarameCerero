package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myproyectofinal_din_carloscaramecerero.R
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import com.example.myproyectofinal_din_carloscaramecerero.utils.TaskCard
import com.example.myproyectofinal_din_carloscaramecerero.utils.AddTaskDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import coil.compose.AsyncImage
import java.time.LocalDate
import kotlin.random.Random

/**
 * Pantalla de tutor de la aplicación.
 *
 * Aparecerá en la bottombar solo para aquellos usuarios que tengan el rol de tutor.De lo contrario,
 * esta pantalla no será accesible ni visible.
 *
 * En ella apareceran los tutorizados que el tutor tenga regisytrados, pudiendo ver su progreso,
 * tareas pendientes, y otras métricas relevantes.
 *
 * Tambien podrá añadirles tareas o eventos a su calendario.
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TutorScreen(
    tutorEmail: String
) {
    val ctx = LocalContext.current

    // cargar perfil del tutor
    var tutor by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(tutorEmail) {
        tutor = AppRepository.loadUser(ctx, tutorEmail)
    }

    // Lista de usuarios (no admins)
    var users by remember { mutableStateOf(listOf<User>()) }
    var tutorizados by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(tutorEmail) {
        // cargar todos los usuarios y filtrar no admin
        val all = AppRepository.listAllUsers(ctx).filter { !it.esAdmin && it.allowTutoring }
        users = all.filter { it.email != tutorEmail }
        tutorizados = AppRepository.loadTutorizados(ctx, tutorEmail)
    }

    // estado para crear nuevo tutorizado
    var showAddTutorizado by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
    ) {
        Text(text = "Gestión de tutorizados", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (tutor == null) {
            Text(text = "Cargando usuario tutor...", color = Color.Gray)
            return@Column
        }

        if (tutor?.esAdmin != true) {
            Text(text = "No autorizado: necesita permisos de tutor/administrador.", color = Color.Red)
            return@Column
        }

        Spacer(modifier = Modifier.height(8.dp))

        // boton para añadir tutorizado
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showAddTutorizado = true }) { Text("Añadir tutorizado") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (users.isEmpty()) {
            Text(text = "Aún no hay usuarios disponibles.", color = Color.Gray)
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(users, key = { it.email }) { u ->
                // estado local por item para expandir
                var expanded by remember { mutableStateOf(false) }
                var tasks by remember { mutableStateOf(listOf<Task>()) }
                var events by remember { mutableStateOf(listOf<CalendarEvent>()) }

                Card(modifier = Modifier
                    .fillMaxWidth()
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expanded = !expanded
                            if (expanded) {
                                tasks = AppRepository.loadTasks(ctx, u.email)
                                events = AppRepository.loadEvents(ctx, u.email)
                            }
                        }
                        .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // avatar
                            Box(modifier = Modifier.size(48.dp)) {
                                if (u.avatarUri != null) {
                                    AsyncImage(model = u.avatarUri, contentDescription = "Avatar", modifier = Modifier.fillMaxSize())
                                } else if (u.avatarRes != 0) {
                                    Image(painter = painterResource(u.avatarRes), contentDescription = "Avatar", modifier = Modifier.fillMaxSize())
                                } else {
                                    Image(painter = painterResource(R.drawable.pfp), contentDescription = "Avatar", modifier = Modifier.fillMaxSize())
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = u.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = u.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }

                            val isAdded = tutorizados.contains(u.email)
                            if (isAdded) {
                                TextButton(onClick = {
                                    AppRepository.removeTutorizado(ctx, tutorEmail, u.email)
                                    tutorizados = tutorizados.filterNot { it == u.email }
                                }) {
                                    Text("Eliminado")
                                }
                            } else {
                                TextButton(onClick = {
                                    AppRepository.addTutorizado(ctx, tutorEmail, u.email)
                                    tutorizados = tutorizados + u.email
                                }) {
                                    Text("Agregar")
                                }
                            }
                        }

                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))

                            // Tareas
                            Text(text = "Tareas (${tasks.size})", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                tasks.forEach { t ->
                                    TaskCard(task = t, onStatusChange = { id, newStatus ->
                                        tasks = tasks.map { if (it.id == id) it.copy(status = newStatus) else it }
                                        AppRepository.saveTasks(ctx, u.email, tasks)
                                    }, onDelete = { id ->
                                        tasks = tasks.filterNot { it.id == id }
                                        AppRepository.saveTasks(ctx, u.email, tasks)
                                    })
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Eventos
                            Text(text = "Eventos (${events.size})", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                events.forEach { ev ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text(text = ev.title)
                                            Text(text = ev.date.toString() + (if (!ev.time.isNullOrBlank()) " ${ev.time}" else ""), color = Color.Gray)
                                        }
                                        IconButton(onClick = {
                                            events = events.filterNot { it.id == ev.id }
                                            AppRepository.saveEvents(ctx, u.email, events)
                                        }) {
                                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar evento", tint = Color(0xFFB00020))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Botones para añadir tarea/evento
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                var showAddTaskDialog by remember { mutableStateOf(false) }
                                var showAddEventDialog by remember { mutableStateOf(false) }

                                TextButton(onClick = { showAddTaskDialog = true }) { Text("Añadir tarea") }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = { showAddEventDialog = true }) { Text("Añadir evento") }

                                if (showAddTaskDialog) {
                                    AddTaskDialog(onDismiss = { showAddTaskDialog = false }, onTaskAdded = { t ->
                                        val newTask = t.copy(id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE))
                                        tasks = tasks + newTask
                                        AppRepository.saveTasks(ctx, u.email, tasks)
                                        showAddTaskDialog = false
                                    })
                                }

                                if (showAddEventDialog) {
                                    var title by remember { mutableStateOf("") }
                                    var time by remember { mutableStateOf<String?>(null) }
                                    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
                                    val localCtx = LocalContext.current

                                    // DatePickerDialog para seleccionar fecha
                                    val dpd = android.app.DatePickerDialog(
                                        localCtx,
                                        { _, year, month, dayOfMonth ->
                                            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                        },
                                        selectedDate.year,
                                        selectedDate.monthValue - 1,
                                        selectedDate.dayOfMonth
                                    )

                                    AlertDialog(
                                        onDismissRequest = { showAddEventDialog = false },
                                        title = { Text("Nuevo evento") },
                                        text = {
                                            Column {
                                                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("Fecha: ${selectedDate}", modifier = Modifier.weight(1f))
                                                    TextButton(onClick = { dpd.show() }) { Text("Seleccionar fecha") }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                OutlinedTextField(value = time ?: "", onValueChange = { time = it.ifBlank { null } }, label = { Text("Hora (HH:mm) opcional") })
                                            }
                                        },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                if (title.isNotBlank()) {
                                                    val ev = CalendarEvent(id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE), date = selectedDate, title = title.trim(), time = time)
                                                    events = events + ev
                                                    AppRepository.saveEvents(ctx, u.email, events)
                                                }
                                                showAddEventDialog = false
                                            }) { Text("Guardar") }
                                        },
                                        dismissButton = { TextButton(onClick = { showAddEventDialog = false }) { Text("Cancelar") } }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo para añadir un tutorizado (básico: nombre/email y contraseña)
        if (showAddTutorizado) {
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddTutorizado = false },
                title = { Text("Añadir tutorizado") },
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña (temporal)") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (email.isNotBlank()) {
                            val newUser = User(name = name.ifBlank { email.substringBefore("@") }, email = email, avatarRes = 0, avatarUri = null, esAdmin = false)
                            AppRepository.saveUser(ctx, newUser)
                            if (password.isNotBlank()) AppRepository.saveCredentials(ctx, email, password)
                            // actualizar lista
                            users = users + newUser
                        }
                        showAddTutorizado = false
                    }) { Text("Crear") }
                },
                dismissButton = { TextButton(onClick = { showAddTutorizado = false }) { Text("Cancelar") } }
            )
        }
    }
}
