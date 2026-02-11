package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository // <-- nuevo
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myproyectofinal_din_carloscaramecerero.utils.SummaryCard // <-- importar el componente
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Checkbox
import com.example.myproyectofinal_din_carloscaramecerero.utils.ReportGenerator
import com.example.myproyectofinal_din_carloscaramecerero.utils.ReportChart
import com.example.myproyectofinal_din_carloscaramecerero.model.ReportFilters
import com.example.myproyectofinal_din_carloscaramecerero.model.ReportPeriod
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.clip
import com.example.myproyectofinal_din_carloscaramecerero.model.ReportSummary
import androidx.compose.ui.graphics.lerp

/**
 * Pantalla principal (Home) que muestra un saludo personalizado y un resumen rápido
 * de items por el usuario actual.
 *
 * Carga tareas, eventos y colecciones usando [AppRepository] según el email del [user].
 * Dispone de tarjetas presionables que expanden su contenido (tareas, eventos, colecciones).
 *
 * @param user Perfil del usuario actualmente logueado.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(user: User, isLightFilter: Boolean = false) { // <-- ahora recibe el usuario y el estado del filtro claro
    val ctx = LocalContext.current
    val today = remember { LocalDate.now() }
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var eventsForToday by remember { mutableStateOf(listOf<String>()) } // título de evento
    var collectionsCount by remember { mutableIntStateOf(0) }

    // estado para sección expandida
    var expandedSection by remember { mutableStateOf<String?>(null) }

    // estado para informe
    var showReportDialog by remember { mutableStateOf(false) }
    var showReportResult by remember { mutableStateOf<String?>(null) }
    var reportFilters by remember { mutableStateOf(ReportFilters()) }

    val scrollState = rememberScrollState()

    // Cargar datos por perfil usando AppRepository cuando cambie el usuario
    LaunchedEffect(user.email) {
        if (user.email.isBlank()) {
            tasks = emptyList()
            eventsForToday = emptyList()
            collectionsCount = 0
        } else {
            // cargar tareas
            tasks = AppRepository.loadTasks(ctx, user.email)
            // cargar eventos y filtrar los de hoy
            val loadedEvents = AppRepository.loadEvents(ctx, user.email)
            eventsForToday = loadedEvents.filter { it.date.toString() == today.toString() }.map { it.title }
            // cargar colecciones y contar
            val loadedCols = AppRepository.loadCollections(ctx, user.email)
            collectionsCount = loadedCols.size
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState) // permitir scroll en toda la pantalla
    ) {
        // Saludo y fecha: incluir nombre del usuario
        Text(
            text = "Bienvenido, ${user.name}",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Resumen rápido: apilado verticalmente para que las cards no se compriman
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                icon = { Icon(Icons.Default.List, contentDescription = "Tareas") },
                title = "Tareas hoy",
                value = tasks.size.toString(),
                onClick = { // alternar expansión de tareas
                    expandedSection = if (expandedSection == "tasks") null else "tasks"
                }
            )
            SummaryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                icon = { Icon(Icons.Default.Event, contentDescription = "Eventos") },
                title = "Eventos hoy",
                value = eventsForToday.size.toString(),
                onClick = { // alternar expansión de eventos
                    expandedSection = if (expandedSection == "events") null else "events"
                }
            )
            SummaryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                icon = { Icon(Icons.Default.VideoLibrary, contentDescription = "Colecciones") },
                title = "Colecciones",
                value = collectionsCount.toString(),
                onClick = { // alternar expansión de colecciones
                    expandedSection = if (expandedSection == "collections") null else "collections"
                }
            )
            // Botón generar informe reubicado: aparece debajo del card de Colecciones
            Button(onClick = { showReportDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Generar informe")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mostrar contenido expandido según la card seleccionada
        when (expandedSection) {
            "tasks" -> {
                Text("Tareas", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                val tasksToShow = tasks.take(12)
                if (tasksToShow.isEmpty()) {
                    Text("No hay tareas registradas.", color = Color.Gray)
                } else {
                    // usar Column con scroll interno limitado para evitar anidamiento de LazyColumn dentro de scroll padre
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                    ) {
                        tasksToShow.forEach { t ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = t.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        if (t.description.isNotBlank()) {
                                            Text(text = t.description, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        }
                                    }
                                    Text(text = t.status.desc, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            "events" -> {
                Text("Eventos de hoy", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                if (eventsForToday.isEmpty()) {
                    Text("No hay eventos programados para hoy.", color = Color.Gray)
                } else {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .verticalScroll(rememberScrollState())
                    ) {
                        eventsForToday.take(12).forEach { ev ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = ev, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            "collections" -> {
                Text("Colecciones de vídeos", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Tiene $collectionsCount colecciones. Pulse 'Colecciones' otra vez para cerrar.", color = Color.Gray)
            }

            else -> {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // ------------------ BLOQUE DEL INFORME: colocado bajo las cards/expandables ------------------
        showReportResult?.let { reportText ->
            // elegir color del fondo según filtro claro
            val cardGray = if (isLightFilter) Color(0xFFE6E1E8) else Color(0xFF35343A)

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(cardGray)
                .padding(8.dp)
            ) {
                Text("Informe generado", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(8.dp))

                // Bloque del texto del informe: mostrar completo en pantalla (la pantalla principal es la que hace scroll)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                ) {
                    Text(reportText, color = MaterialTheme.colorScheme.onSurface)
                }

                // Generar y mostrar gráfico resumen
                val summary = remember { mutableStateOf<ReportSummary?>(null) }
                LaunchedEffect(reportText) {
                    summary.value = ReportGenerator.buildReportSummary(ctx, user.email, reportFilters)
                }
                summary.value?.let { s ->
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Gráfico", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(start = 4.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    ReportChart(summary = s, filters = reportFilters, isLightFilter = isLightFilter, modifier = Modifier.fillMaxWidth())
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Informe", reportText)
                        clipboard.setPrimaryClip(clip)
                    }) { Text("Copiar") }

                    TextButton(onClick = {
                        val filename = "report_${user.email}_${System.currentTimeMillis()}.txt"
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

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Mostrar dialogo de filtros para generar informe (mejor espaciado)
        if (showReportDialog) {
            AlertDialog(onDismissRequest = { showReportDialog = false },
                title = { Text("Generar informe") },
                text = {
                    // permitir scroll dentro del dialog si la pantalla es pequeña
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Periodo:")
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = reportFilters.period == ReportPeriod.LAST_WEEK, onClick = { reportFilters = reportFilters.copy(period = ReportPeriod.LAST_WEEK) })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(ReportPeriod.LAST_WEEK.desc)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = reportFilters.period == ReportPeriod.LAST_MONTH, onClick = { reportFilters = reportFilters.copy(period = ReportPeriod.LAST_MONTH) })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(ReportPeriod.LAST_MONTH.desc)
                            }
                        }

                        Text("Incluir:")
                        // cada opción en su propia fila para evitar textos que se deformen
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Checkbox(checked = reportFilters.includeCompleted, onCheckedChange = { reportFilters = reportFilters.copy(includeCompleted = it) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Completadas")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Checkbox(checked = reportFilters.includeInProgress, onCheckedChange = { reportFilters = reportFilters.copy(includeInProgress = it) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("En proceso")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Checkbox(checked = reportFilters.includePending, onCheckedChange = { reportFilters = reportFilters.copy(includePending = it) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pendientes")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Checkbox(checked = reportFilters.includeEvents, onCheckedChange = { reportFilters = reportFilters.copy(includeEvents = it) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eventos")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Checkbox(checked = reportFilters.includeVideos, onCheckedChange = { reportFilters = reportFilters.copy(includeVideos = it) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Videos")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showReportResult = ReportGenerator.buildReportText(ctx, user.email, reportFilters)
                        showReportDialog = false
                    }) { Text("Generar") }
                },
                dismissButton = { TextButton(onClick = { showReportDialog = false }) { Text("Cancelar") } }
            )
        }

    }
}
