package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.myproyectofinal_din_carloscaramecerero.utils.AddButtonBlue
import com.example.myproyectofinal_din_carloscaramecerero.utils.PrimaryBlue
import com.example.myproyectofinal_din_carloscaramecerero.utils.CalendarioGrid
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.snapshotFlow
import androidx.core.content.edit

// <-- Nuevas constantes y helpers para persistencia de eventos -->
private const val PREFS_EVENTS = "calendar_events_prefs"
private const val EVENTS_KEY = "events_serialized"

private fun serializeEvents(events: List<Pair<LocalDate, String>>): String =
    events.joinToString("|||") { "${it.first}::${it.second.replace("::", " ")}" }

@RequiresApi(Build.VERSION_CODES.O)
private fun deserializeEvents(serialized: String?): List<Pair<LocalDate, String>> {
    if (serialized.isNullOrEmpty()) return emptyList()
    return try {
        serialized.split("|||").mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size < 2) return@mapNotNull null
            val date = try { LocalDate.parse(parts[0]) } catch (_: Exception) { return@mapNotNull null }
            val title = parts.subList(1, parts.size).joinToString("::")
            Pair(date, title)
        }
    } catch (_: Exception) {
        emptyList()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen() {
    val context = LocalContext.current

    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }
    val eventsMap = remember { mutableStateListOf<Pair<LocalDate, String>>() }
    var showAddDialog by remember { mutableStateOf(false) }
    var newEventText by remember { mutableStateOf("") }
    var showEvents by remember { mutableStateOf(false) }

    // Cargar eventos al componer
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(PREFS_EVENTS, Context.MODE_PRIVATE)
        val loaded = deserializeEvents(prefs.getString(EVENTS_KEY, null))
        eventsMap.clear()
        eventsMap.addAll(loaded)
    }

    // Guardar automáticamente cuando cambian los eventos (observando snapshot del listado)
    LaunchedEffect(Unit) {
        snapshotFlow { eventsMap.toList() }.collect { list ->
            val prefs = context.getSharedPreferences(PREFS_EVENTS, Context.MODE_PRIVATE)
            prefs.edit { putString(EVENTS_KEY, serializeEvents(list)) }
        }
    }

    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 120.dp) // aumentado para reservar más espacio al botón flotante
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    val lastDay = currentMonth.atEndOfMonth()
                    if (selectedDate.isAfter(lastDay)) selectedDate = lastDay
                }) {
                    Icon(Icons.Filled.ArrowBackIos, contentDescription = "Mes anterior")
                }

                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    val lastDay = currentMonth.atEndOfMonth()
                    if (selectedDate.isAfter(lastDay)) selectedDate = lastDay
                }) {
                    Icon(Icons.Filled.ArrowForwardIos, contentDescription = "Mes siguiente")
                }
            }

            Spacer(modifier = Modifier.height(4.dp)) // reducido

            CalendarioGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                today = today,
                events = eventsMap.toList(),
                onDateSelected = { date ->
                    if (date == selectedDate) {
                        showEvents = !showEvents
                    } else {
                        selectedDate = date
                        showEvents = true
                    }
                },
                modifier = Modifier
                    .height(260.dp) // altura fija para evitar que el grid se estire y quede cubierto
            )

            Spacer(modifier = Modifier.height(6.dp)) // reducido

            if (showEvents) {
                Text(
                    text = "Eventos: ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                    fontWeight = FontWeight.SemiBold
                )

                val eventsForDay = eventsMap.filter { it.first == selectedDate }.map { it.second }

                // Lista scrollable de eventos (con botón para eliminar cada evento)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    content = {
                        if (eventsForDay.isEmpty()) {
                            item {
                                Text(
                                    "No hay eventos",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        } else {
                            items(eventsForDay) { title ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(color = PrimaryBlue, shape = MaterialTheme.shapes.small)
                                            )
                                            Spacer(modifier = Modifier.size(8.dp))
                                            Text(text = title)
                                        }

                                        // Icono de papelera para eliminar el evento (persistencia automática)
                                        IconButton(onClick = {
                                            // eliminar la primera coincidencia (fecha + título)
                                            eventsMap.remove(Pair(selectedDate, title))
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Eliminar evento",
                                                tint = Color(0xFFB00020)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp)) // reducido
            }
        }

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
                    .background(AddButtonBlue)
                    .clickable { showAddDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir evento",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    if (newEventText.isNotBlank()) {
                        eventsMap.add(Pair(selectedDate, newEventText.trim()))
                        newEventText = ""
                        showEvents = true
                    }
                    showAddDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    newEventText = ""
                }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Nuevo evento") },
            text = {
                Column {
                    Text("Fecha: ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newEventText,
                        onValueChange = { newEventText = it },
                        label = { Text("Título del evento") },
                        singleLine = true
                    )
                }
            }
        )
    }
}
