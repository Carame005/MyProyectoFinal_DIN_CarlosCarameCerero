package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myproyectofinal_din_carloscaramecerero.model.*
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository
import com.example.myproyectofinal_din_carloscaramecerero.utils.*
import java.time.LocalDate
import kotlin.random.Random

/**
 * Pantalla de gestión para tutores.
 *
 * Muestra una lista de usuarios disponibles para ser añadidos como "tutorizados" y permite al
 * tutor ver y gestionar las tareas y eventos de cada tutorizado. Cada usuario se representa mediante
 * `TutorizadoCard`, un componente reutilizable que muestra avatar, nombre, email y acciones.
 *
 * @param tutorEmail Email del usuario con rol de tutor (se usa para cargar su lista de tutorizados y guardar cambios).
 */

@Suppress("NewApi")
@Composable
fun TutorScreen(
    tutorEmail: String,
    isLightFilter: Boolean = false
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
        // ahora los tutores pueden gestionar todos los usuarios con rol 'tutorizado', por lo que
        // cargamos todos los usuarios que no sean admin (sin filtrar por allowTutoring)
        val all = AppRepository.listAllUsers(ctx).filter { !it.esAdmin }
        users = all.filter { it.email != tutorEmail }
        // eliminamos la dependencia de listas de tutorizados: el tutor gestiona todas las cuentas
        tutorizados = emptyList()
    }

    // estado para crear nuevo tutorizado
    // var showAddTutorizado by remember { mutableStateOf(false) }

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
        // Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        //     Button(onClick = { showAddTutorizado = true }) { Text("Añadir tutorizado") }
        // }

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
                var collections by remember { mutableStateOf(listOf<VideoCollection>()) }

                TutorizadoCard(
                    user = u,
                    isAdded = false,
                    expanded = expanded,
                    onAdd = {},
                    onRemove = {},
                    showActions = false,
                    onExpandChange = {
                        expanded = it
                        if (it) {
                            tasks = AppRepository.loadTasks(ctx, u.email)
                            events = AppRepository.loadEvents(ctx, u.email)
                            collections = AppRepository.loadCollections(ctx, u.email)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    expandedContent = {
                        // estado para reproducir vídeos locales en este tutorizado
                        var playingUri by remember { mutableStateOf<String?>(null) }
                        var showAddVideoDialogForCollection by remember { mutableStateOf<Int?>(null) }

                        // Informe states
                        var showTutReportDialog by remember { mutableStateOf(false) }
                        var showTutReportResult by remember { mutableStateOf<String?>(null) }
                        var tutReportFilters by remember { mutableStateOf(ReportFilters()) }
                        val clipboardManager = LocalClipboardManager.current

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
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar evento")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mostrar informe si existe
                        showTutReportResult?.let { reportText ->
                            val cardGray = if (isLightFilter) Color(0xFFE6E1E8) else Color(0xFF35343A)
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cardGray)
                                .padding(8.dp)
                            ) {
                                Text("Informe generado", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp))

                                // Mostrar el informe completo aquí (la pantalla principal controla el scroll)
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                ) {
                                    Text(reportText, color = MaterialTheme.colorScheme.onSurface)
                                }

                                // Generar y mostrar gráfico resumen
                                val tutSummary = remember { mutableStateOf<com.example.myproyectofinal_din_carloscaramecerero.model.ReportSummary?>(null) }
                                LaunchedEffect(reportText) {
                                    tutSummary.value = ReportGenerator.buildReportSummary(ctx, u.email, tutReportFilters)
                                }
                                tutSummary.value?.let { s ->
                                     Spacer(modifier = Modifier.height(8.dp))
                                     androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), thickness = 1.dp)
                                     Spacer(modifier = Modifier.height(6.dp))
                                     Text(text = "Gráfico", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 4.dp))
                                     Spacer(modifier = Modifier.height(6.dp))
                                    ReportChart(summary = s, filters = tutReportFilters, isLightFilter = isLightFilter, modifier = Modifier.fillMaxWidth())
                                 }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(onClick = { clipboardManager.setText(AnnotatedString(reportText)) }) { Text("Copiar") }
                                    TextButton(onClick = {
                                        val filename = "report_${u.email}_${System.currentTimeMillis()}.txt"
                                        val path = ReportGenerator.saveReportToCache(ctx, filename, reportText)
                                        if (path != null) {
                                            val uri = ReportGenerator.getUriForFile(ctx, path)
                                            val share = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            ctx.startActivity(android.content.Intent.createChooser(share, "Compartir informe"))
                                        }
                                    }) { Text("Compartir") }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Dialogo de filtros para informes del tutorizado (cada opción en su propia fila para evitar deformaciones)
                        if (showTutReportDialog) {
                            AlertDialog(onDismissRequest = { showTutReportDialog = false },
                                title = { Text("Generar informe de ${u.name}") },
                                text = {
                                    Column(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("Periodo:")
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = tutReportFilters.period == ReportPeriod.LAST_WEEK, onClick = { tutReportFilters = tutReportFilters.copy(period = ReportPeriod.LAST_WEEK) })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(ReportPeriod.LAST_WEEK.desc)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = tutReportFilters.period == ReportPeriod.LAST_MONTH, onClick = { tutReportFilters = tutReportFilters.copy(period = ReportPeriod.LAST_MONTH) })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(ReportPeriod.LAST_MONTH.desc)
                                        }

                                        Text("Incluir:")
                                        // filas separadas para cada checkbox (evita que varias etiquetas queden en la misma línea y se deformen)
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                            Checkbox(checked = tutReportFilters.includeCompleted, onCheckedChange = { tutReportFilters = tutReportFilters.copy(includeCompleted = it) })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Completadas")
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                            Checkbox(checked = tutReportFilters.includeInProgress, onCheckedChange = { tutReportFilters = tutReportFilters.copy(includeInProgress = it) })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("En proceso")
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                            Checkbox(checked = tutReportFilters.includePending, onCheckedChange = { tutReportFilters = tutReportFilters.copy(includePending = it) })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Pendientes")
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                            Checkbox(checked = tutReportFilters.includeEvents, onCheckedChange = { tutReportFilters = tutReportFilters.copy(includeEvents = it) })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Eventos")
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                            Checkbox(checked = tutReportFilters.includeVideos, onCheckedChange = { tutReportFilters = tutReportFilters.copy(includeVideos = it) })
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Videos")
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showTutReportResult = ReportGenerator.buildReportText(ctx, u.email, tutReportFilters)
                                        showTutReportDialog = false
                                    }) { Text("Generar") }
                                },
                                dismissButton = { TextButton(onClick = { showTutReportDialog = false }) { Text("Cancelar") } }
                            )
                        }

                        // Vídeos / colecciones gestionados por tutor
                        Text(text = "Colecciones de vídeo (${collections.size})", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            collections.forEach { col ->
                                var expandedCol by remember { mutableStateOf(false) }
                                CollectionCard(
                                    collection = col,
                                    expanded = expandedCol,
                                    onToggleExpanded = { expandedCol = !expandedCol },
                                    onAddVideo = {
                                        // abrir diálogo para añadir vídeo a esta colección
                                        showAddVideoDialogForCollection = col.id
                                    },
                                    onDeleteCollection = {
                                        collections = collections.filterNot { it.id == col.id }
                                        AppRepository.saveCollections(ctx, u.email, collections)
                                    },
                                    onDeleteVideo = { vidId ->
                                        collections = collections.map {
                                            if (it.id == col.id) it.copy(items = it.items.filterNot { v-> v.id == vidId }) else it
                                        }
                                        AppRepository.saveCollections(ctx, u.email, collections)
                                    },
                                    onPlayVideo = { uriStr ->
                                        val lower = uriStr.lowercase()
                                        if ((lower.startsWith("http://") || lower.startsWith("https://")) && (lower.contains("youtube.com") || lower.contains("youtu.be"))) {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                                ctx.startActivity(intent)
                                            } catch (_: Exception) { /* ignore */ }
                                        } else {
                                            playingUri = uriStr
                                        }
                                    },
                                    canDeleteCollection = true,
                                    canDeleteVideo = true
                                )

                                // reproducir diálogo para local URIs
                                if (playingUri != null) {
                                    VideoPlayerDialog(uriString = playingUri!!, onClose = { playingUri = null })
                                }
                            } // cierre de collections.forEach
                        } // cierre del Column(verticalArrangement = ...)

                        // Botón ver/generar informes (reubicado) debajo de las colecciones
                        Button(onClick = { showTutReportDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Informe")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón de acciones (menú desplegable) en vez de mostrar muchos botones en fila
                        // declaramos los estados de los diálogos aquí
                        var showAddTaskDialog by remember { mutableStateOf(false) }
                        var showAddEventDialog by remember { mutableStateOf(false) }
                        var showAddCollectionDialog by remember { mutableStateOf(false) }
                        var showResetPasswordDialog by remember { mutableStateOf(false) }
                        var resetResultMsg by remember { mutableStateOf("") }

                        var actionsExpanded by remember { mutableStateOf(false) }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            IconButton(onClick = { actionsExpanded = true }) {
                                androidx.compose.material3.Icon(imageVector = androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "Acciones")
                            }
                            DropdownMenu(expanded = actionsExpanded, onDismissRequest = { actionsExpanded = false }) {
                                DropdownMenuItem(text = { Text("Restablecer contraseña") }, onClick = { actionsExpanded = false; showResetPasswordDialog = true })
                                DropdownMenuItem(text = { Text("Añadir tarea") }, onClick = { actionsExpanded = false; showAddTaskDialog = true })
                                DropdownMenuItem(text = { Text("Añadir evento") }, onClick = { actionsExpanded = false; showAddEventDialog = true })
                                DropdownMenuItem(text = { Text("Añadir colección de vídeos") }, onClick = { actionsExpanded = false; showAddCollectionDialog = true })
                            }

                            // Diálogos asociados (mismos que antes, se abren por el menú)
                            if (showResetPasswordDialog) {
                                var newPw by remember { mutableStateOf("") }
                                var confirmPw by remember { mutableStateOf("") }
                                var errorMsg by remember { mutableStateOf("") }

                                AlertDialog(
                                    onDismissRequest = { showResetPasswordDialog = false },
                                    title = { Text("Restablecer contraseña para ${u.name}") },
                                    text = {
                                        Column {
                                            OutlinedTextField(value = newPw, onValueChange = { newPw = it }, label = { Text("Nueva contraseña") })
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = confirmPw, onValueChange = { confirmPw = it }, label = { Text("Confirmar contraseña") })
                                            if (errorMsg.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(text = errorMsg, color = MaterialTheme.colorScheme.error)
                                            }
                                            if (resetResultMsg.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(text = resetResultMsg, color = Color.Green)
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            errorMsg = ""
                                            resetResultMsg = ""
                                            if (newPw.length < 4) {
                                                errorMsg = "La contraseña debe tener al menos 4 caracteres"
                                                return@TextButton
                                            }
                                            if (newPw != confirmPw) {
                                                errorMsg = "Las contraseñas no coinciden"
                                                return@TextButton
                                            }
                                            try {
                                                AppRepository.saveCredentials(ctx, u.email, newPw)
                                                resetResultMsg = "Contraseña restablecida correctamente"
                                                showResetPasswordDialog = false
                                            } catch (_: Exception) {
                                                errorMsg = "Error al guardar la contraseña"
                                            }
                                        }) { Text("Guardar") }
                                    },
                                    dismissButton = { TextButton(onClick = { showResetPasswordDialog = false }) { Text("Cancelar") } }
                                )
                            }


                            if (showAddTaskDialog) {
                                AddTaskDialog(onDismiss = { showAddTaskDialog = false }, onTaskAdded = { t ->
                                    val newTask = t.copy(id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE), createdByTutor = true)
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
                                                val ev = CalendarEvent(id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE), date = selectedDate, title = title.trim(), time = time, createdByTutor = true)
                                                events = events + ev
                                                AppRepository.saveEvents(ctx, u.email, events)
                                            }
                                            showAddEventDialog = false
                                        }) { Text("Guardar") }
                                    },
                                    dismissButton = { TextButton(onClick = { showAddEventDialog = false }) { Text("Cancelar") } }
                                )
                            }

                            if (showAddCollectionDialog) {
                                var title by remember { mutableStateOf(TextFieldValue("")) }
                                AlertDialog(
                                    onDismissRequest = { showAddCollectionDialog = false },
                                    title = { Text("Nueva colección de vídeos") },
                                    text = {
                                        Column {
                                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Cada colección agrupa varios vídeos que podrá reproducir fácilmente.")
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            val newCol = VideoCollection(id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE), title = title.text.ifBlank { "Colección ${Random.nextInt(1000)}" }, items = emptyList())
                                            collections = collections + newCol
                                            AppRepository.saveCollections(ctx, u.email, collections)
                                            showAddCollectionDialog = false
                                        }) { Text("Crear") }
                                    },
                                    dismissButton = { TextButton(onClick = { showAddCollectionDialog = false }) { Text("Cancelar") } }
                                )
                            }

                            if (showAddVideoDialogForCollection != null) {
                                val colId = showAddVideoDialogForCollection!!
                                var title by remember { mutableStateOf(TextFieldValue("")) }
                                var desc by remember { mutableStateOf(TextFieldValue("")) }
                                var url by remember { mutableStateOf("") }
                                var selectedUriLocal by remember { mutableStateOf<Uri?>(null) }
                                var showError by remember { mutableStateOf(false) }
                                var errorMessage by remember { mutableStateOf("") }

                                val pickLauncher = rememberLauncherForActivityResult(
                                    ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                                    if (uri != null) {
                                        try { ctx.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
                                        selectedUriLocal = uri
                                    }
                                }

                                // Limpiar errores cuando el usuario cambia la selección o la URL
                                LaunchedEffect(selectedUriLocal, url) {
                                    if (showError) {
                                        showError = false
                                        errorMessage = ""
                                    }
                                }

                                AlertDialog(
                                    onDismissRequest = { showAddVideoDialogForCollection = null },
                                    title = { Text("Añadir vídeo") },
                                    text = {
                                        Column {
                                            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                TextButton(onClick = { pickLauncher.launch(arrayOf("video/*")) }) { Text("Seleccionar vídeo") }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = selectedUriLocal?.lastPathSegment ?: (selectedUriLocal?.toString() ?: "Ninguno seleccionado"))
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL (https://...)") })

                                            if (showError) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(text = errorMessage.ifBlank { "Debe seleccionar un vídeo local o proporcionar una URL válida." }, color = Color(0xFFB00020))
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            // validar no permitir ambas fuentes a la vez
                                            val hasLocal = selectedUriLocal != null
                                            val hasUrl = url.trim().isNotBlank()
                                            if (hasLocal && hasUrl) {
                                                errorMessage = "Seleccione solo una fuente: o vídeo local o URL, no ambos."
                                                showError = true
                                                return@TextButton
                                            }

                                            val uriStr = selectedUriLocal?.toString()?.takeIf { it.isNotBlank() } ?: url.trim().takeIf { it.isNotBlank() }
                                            if (uriStr == null) {
                                                errorMessage = "Debe seleccionar un vídeo local o proporcionar una URL válida."
                                                showError = true
                                                return@TextButton
                                            }

                                            val newItem = VideoItem(id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE), title = title.text.ifBlank { "Vídeo ${Random.nextInt(1000)}" }, description = desc.text, uriString = uriStr, createdByTutor = true)
                                            collections = collections.map { if (it.id == colId) it.copy(items = it.items + newItem) else it }
                                            AppRepository.saveCollections(ctx, u.email, collections)
                                            showAddVideoDialogForCollection = null
                                        }) { Text("Añadir") }
                                    },
                                    dismissButton = { TextButton(onClick = { showAddVideoDialogForCollection = null }) { Text("Cancelar") } }
                                )
                            }

                        }
                    }
                )
            }
        }

        // Diálogo para añadir un tutorizado (básico: nombre/email y contraseña)
        // La opción de crear tutorizados se ha eliminado: los tutores gestionan todos los usuarios
    }
}
