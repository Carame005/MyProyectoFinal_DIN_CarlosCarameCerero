package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.Manifest
import android.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
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
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository // <-- nuevo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.myproyectofinal_din_carloscaramecerero.utils.AddButtonBlue
import com.example.myproyectofinal_din_carloscaramecerero.utils.PrimaryBlue
import com.example.myproyectofinal_din_carloscaramecerero.utils.CalendarioGrid
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent // <-- usar la data class del modelo
import kotlin.random.Random
import com.example.myproyectofinal_din_carloscaramecerero.model.User

// Constantes de persistencia y notificación
private const val PREFS_EVENTS = "calendar_events_prefs"
private const val EVENTS_KEY = "events_serialized"
private const val NOTIF_CHANNEL_ID = "calendar_events_channel"
private const val NOTIF_CHANNEL_NAME = "Eventos del calendario"

/**
 * Serializa una lista de [CalendarEvent] a un String plano para persistencia sencilla.
 * Usa separadores '|||' y '::' para reconstruir posteriormente.
 */
private fun serializeEvents(events: List<CalendarEvent>): String =
    events.joinToString("|||") {
        val timePart = it.time ?: ""
        "${it.id}::${it.date}::${it.title.replace("::", " ")}::${timePart}"
    }

/**
 * Deserializa el contenido serializado hacia una lista de [CalendarEvent].
 * Devuelve lista vacía en caso de error o entrada nula.
 */
private fun deserializeEvents(serialized: String?): List<CalendarEvent> {
    if (serialized.isNullOrEmpty()) return emptyList()
    return try {
        serialized.split("|||").mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size < 4) return@mapNotNull null
            val id = parts[0].toIntOrNull() ?: return@mapNotNull null
            val date = try { LocalDate.parse(parts[1]) } catch (_: Exception) { return@mapNotNull null }
            val title = parts[2]
            val time = parts[3].ifBlank { null }
            CalendarEvent(id = id, date = date, title = title, time = time)
        }
    } catch (_: Exception) {
        emptyList()
    }
}

/**
 * BroadcastReceiver que se encarga de mostrar la notificación cuando suena la alarma
 * programada para un evento de calendario.
 *
 * Requiere permiso POST_NOTIFICATIONS en Android 13+.
 */
class AlarmReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getIntExtra("eventId", 0)
        val title = intent.getStringExtra("title") ?: "Evento"
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""

        ensureNotificationChannel(context)

        val notif = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio: $title")
            .setContentText("Fecha: $date ${if (time.isNotBlank()) "Hora: $time" else ""}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(eventId, notif)
    }
}

/**
 * Crea el canal de notificación necesario para los recordatorios del calendario.
 */
private fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channel = nm.getNotificationChannel(NOTIF_CHANNEL_ID)
        if (channel == null) {
            val newChannel = android.app.NotificationChannel(
                NOTIF_CHANNEL_ID,
                NOTIF_CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(newChannel)
        }
    }
}

/**
 * Programa una alarma exacta para el [event] en la hora/date definida.
 * No hace nada si [event.time] es null o la hora ya pasó.
 */
private fun scheduleAlarm(context: Context, event: CalendarEvent) {
    if (event.time.isNullOrBlank()) return
    try {
        val localTime = LocalTime.parse(event.time)
        val dateTime = LocalDateTime.of(event.date, localTime)
        val triggerAt = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (triggerAt <= System.currentTimeMillis()) return // no programar pasado

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("eventId", event.id)
            putExtra("title", event.title)
            putExtra("date", event.date.toString())
            putExtra("time", event.time ?: "")
        }
        val pending = PendingIntent.getBroadcast(
            context,
            event.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    } catch (_: Exception) {
        // ignore parse errors
    }
}

/**
 * Cancela la alarma asociada al identificador [eventId] si existe.
 */
private fun cancelAlarm(context: Context, eventId: Int) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_NO_CREATE or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        if (pending != null) {
            alarmManager.cancel(pending)
            pending.cancel()
        }
    } catch (_: Exception) {
        // ignore
    }
}

/**
 * Composable principal de la pantalla de calendario.
 *
 * @param userEmail Email del usuario actual — usado para cargar/guardar eventos por perfil.
 * Muestra un calendario mensual navegable, lista de eventos del día seleccionado y diálogo
 * para crear eventos con hora (opcional) que programará notificaciones.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(userEmail: String) { // <-- ahora recibe userEmail
    val context = LocalContext.current

    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    val eventsMap = remember { mutableStateListOf<CalendarEvent>() }
    var showAddDialog by remember { mutableStateOf(false) }
    var newEventText by remember { mutableStateOf("") }
    var newEventTime by remember { mutableStateOf<String?>(null) }
    var showEvents by remember { mutableStateOf(false) }

    // Cargar eventos al componer (desde AppRepository por usuario)
    LaunchedEffect(userEmail) {
        val loaded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppRepository.loadEvents(context, userEmail)
        } else emptyList()
        eventsMap.clear()
        eventsMap.addAll(loaded)
        ensureNotificationChannel(context)
        loaded.forEach { ev -> scheduleAlarm(context, ev) }
    }

    // Guardar automáticamente cuando cambian los eventos
    LaunchedEffect(Unit) {
        snapshotFlow { eventsMap.toList() }.collect { list ->
            AppRepository.saveEvents(context, userEmail, list)
        }
    }

    // cargar usuario actual para comprobar rol
    var currentUser by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(userEmail) { currentUser = AppRepository.loadUser(context, userEmail) }
    val isAdmin = currentUser?.esAdmin == true

    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 120.dp)
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

            Spacer(modifier = Modifier.height(4.dp))

            CalendarioGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                today = today,
                // adaptamos events para el grid
                events = eventsMap.map { Pair(it.date, it.title) }, // CalendarioGrid solo usa fecha para marcadores
                onDateSelected = { date ->
                    if (date == selectedDate) {
                        showEvents = !showEvents
                    } else {
                        selectedDate = date
                        showEvents = true
                    }
                },
                modifier = Modifier
                    .height(260.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (showEvents) {
                Text(
                    text = "Eventos: ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                    fontWeight = FontWeight.SemiBold
                )

                val eventsForDay = eventsMap.filter { it.date == selectedDate }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    if (eventsForDay.isEmpty()) {
                        item {
                            Text(
                                "No hay eventos",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    } else {
                        items(eventsForDay, key = { it.id }) { ev ->
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
                                        Column {
                                            Text(text = ev.title)
                                            if (!ev.time.isNullOrBlank()) {
                                                Text(text = "Hora: ${ev.time}", color = Color.Gray)
                                            }
                                        }
                                    }

                                    // Icono de papelera para eliminar el evento (y cancelar alarma)
                                    if (isAdmin) {
                                        IconButton(onClick = {
                                            eventsMap.remove(ev)
                                            cancelAlarm(context, ev.id)
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
                }

                Spacer(modifier = Modifier.height(8.dp))
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
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Añadir evento",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (showAddDialog) {
        // TimePicker: abrir desde Android TimePickerDialog; almacenamos newEventTime en formato "HH:mm"
        val ctx = LocalContext.current
        val now = LocalTime.now()
        fun showTimePicker() {
            TimePickerDialog(ctx, { _, hour, minute ->
                newEventTime = String.format("%02d:%02d", hour, minute)
            }, now.hour, now.minute, true).show()
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    if (newEventText.isNotBlank()) {
                        val newEv = CalendarEvent(
                            id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                            date = selectedDate,
                            title = newEventText.trim(),
                            time = newEventTime
                        )
                        eventsMap.add(newEv)
                        // programar alarma si tiene hora
                        scheduleAlarm(ctx, newEv)
                        newEventText = ""
                        newEventTime = null
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
                    newEventTime = null
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { showTimePicker() }) {
                            Text(if (newEventTime == null) "Seleccionar hora (opcional)" else "Hora: $newEventTime")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Si selecciona hora, recibirá una notificación cuando llegue.")
                }
            }
        )
    }
}
