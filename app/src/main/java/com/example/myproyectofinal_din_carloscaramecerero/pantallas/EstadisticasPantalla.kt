package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myproyectofinal_din_carloscaramecerero.model.AbstinenceRecord
import com.example.myproyectofinal_din_carloscaramecerero.model.TimeBreakdown
import com.example.myproyectofinal_din_carloscaramecerero.utils.TimeProgressChart
import java.time.Duration
import java.time.Instant
import kotlin.random.Random

private const val PREFS_NAME_STATS = "stats_prefs"
private const val STATS_KEY = "stats_serialized"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsListScreen() {
    val context = LocalContext.current
    var stats by remember { mutableStateOf(listOf<AbstinenceRecord>()) }
    var showDialog by remember { mutableStateOf(false) }
    var expandedId by remember { mutableStateOf<Int?>(null) }

    // Cargar estadísticas al componer
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(PREFS_NAME_STATS, Context.MODE_PRIVATE)
        stats = deserializeStats(prefs.getString(STATS_KEY, null))
    }

    // Guardar cada vez que stats cambian
    LaunchedEffect(stats) {
        val prefs = context.getSharedPreferences(PREFS_NAME_STATS, Context.MODE_PRIVATE)
        prefs.edit().putString(STATS_KEY, serializeStats(stats)).apply()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .padding(bottom = 96.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(items = stats, key = { it.startDate.toString() + it.title }) { record ->
                    var expandedLocal by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                expandedLocal = !expandedLocal
                                expandedId = if (expandedLocal) record.hashCode() else null
                            },
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = record.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${calculateTime(record.startDate).days}d", // pequeño resumen
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            AnimatedVisibility(visible = expandedLocal) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TimeProgressChart(calculateTime(record.startDate))

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Icono de eliminar al expandir
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        IconButton(onClick = {
                                            stats = stats.filterNot { it == record }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar estadística",
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
                    .background(Color(0xFF0D47A1)) // color de contraste (morado oscuro)
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir estadística",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (showDialog) {
        AddStatDialog(
            onDismiss = { showDialog = false },
            onStatAdded = { new ->
                stats = stats + new.copy(startDate = new.startDate) // ya trae fecha
                showDialog = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddStatDialog(
    onDismiss: () -> Unit,
    onStatAdded: (AbstinenceRecord) -> Unit
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val record = AbstinenceRecord(
                    title = if (title.isBlank()) "estadística ${Random.nextInt(1000)}" else title,
                    startDate = Instant.now()
                )
                onStatAdded(record)
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Nueva estadística") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("La fecha de inicio será ahora (puedes editarla luego si implementas esa opción).")
            }
        }
    )
}

// Serialización sencilla
private fun serializeStats(stats: List<AbstinenceRecord>): String =
    stats.joinToString("|||") { "${it.title.replace("::"," ")}::${it.startDate.toString()}" }

@RequiresApi(Build.VERSION_CODES.O)
private fun deserializeStats(serialized: String?): List<AbstinenceRecord> {
    if (serialized.isNullOrEmpty()) return emptyList()
    return try {
        serialized.split("|||").mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size < 2) return@mapNotNull null
            val title = parts[0]
            val start = try { Instant.parse(parts[1]) } catch (_: Exception) { Instant.now() }
            AbstinenceRecord(title = title, startDate = start)
        }
    } catch (_: Exception) {
        emptyList()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateTime(start: Instant, now: Instant = Instant.now()): TimeBreakdown {
    val duration = Duration.between(start, now)

    val hours = duration.toHours()
    val days = hours / 24
    val months = days / 30
    val years = days / 365

    return TimeBreakdown(
        years = years,
        months = months % 12,
        days = days % 30,
        hours = hours % 24
    )
}