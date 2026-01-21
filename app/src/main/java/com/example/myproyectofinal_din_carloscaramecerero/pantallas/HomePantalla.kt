package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository // <-- nuevo
import com.example.myproyectofinal_din_carloscaramecerero.model.Task
import com.example.myproyectofinal_din_carloscaramecerero.model.TaskStatus
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import java.net.URLDecoder
import com.example.myproyectofinal_din_carloscaramecerero.utils.SummaryCard // <-- importar el componente

private const val PREFS_TASKS = "tasks_prefs"
private const val TASKS_KEY = "tasks_serialized"

private const val PREFS_EVENTS = "calendar_events_prefs"
private const val EVENTS_KEY = "events_serialized"

private const val PREFS_COLLECTIONS = "video_collections_prefs"
private const val COLLECTIONS_KEY = "video_collections_serialized"

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
fun HomeScreen(user: User) { // <-- ahora recibe el usuario
    val ctx = LocalContext.current
    val today = remember { LocalDate.now() }
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var eventsForToday by remember { mutableStateOf(listOf<String>()) } // título de evento
    var collectionsCount by remember { mutableIntStateOf(0) }

    // nuevo estado: sección expandida ("tasks" | "events" | "collections" | null)
    var expandedSection by remember { mutableStateOf<String?>(null) }

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
            val loadedEvents = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppRepository.loadEvents(ctx, user.email)
            } else emptyList()
            eventsForToday = loadedEvents.filter { it.date.toString() == today.toString() }.map { it.title }
            // cargar colecciones y contar
            val loadedCols = AppRepository.loadCollections(ctx, user.email)
            collectionsCount = loadedCols.size
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
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
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, false)) {
                        items(tasksToShow) { t ->
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
                    Column(modifier = Modifier.fillMaxWidth()) {
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
                // muñón sencillo: mostrar solo el contador y un texto explicativo; puede reemplazarse por la lista completa
                Text("Tiene $collectionsCount colecciones. Pulse 'Colecciones' otra vez para cerrar.", color = Color.Gray)
            }

            else -> {
                // nada expandido: conservar espacio reducido para que la pantalla no se vea vacía
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // ...si había más contenido original al final, se deja intacto...
    }
}
