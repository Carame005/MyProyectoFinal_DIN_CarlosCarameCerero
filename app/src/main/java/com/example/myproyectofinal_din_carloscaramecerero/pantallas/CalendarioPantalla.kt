package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.Manifest
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
import java.time.YearMonth
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
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import android.util.Log
import com.example.myproyectofinal_din_carloscaramecerero.model.CalendarEvent // <-- usar la data class del modelo
import kotlin.random.Random
import com.example.myproyectofinal_din_carloscaramecerero.model.User

// Reusar helpers públicos para notificaciones/alarms
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.scheduleAlarm
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.cancelAlarm
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.ensureNotificationChannel
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.NOTIF_CHANNEL_ID
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.NOTIF_ACTION

// Nuevos imports para permisos
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.res.Resources
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * BroadcastReceiver que se encarga de mostrar la notificación cuando suena la alarma
 * programada para un evento de calendario.
 *
 * Requiere permiso POST_NOTIFICATIONS en Android 13+.
 */
class AlarmReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        try {
            // solo manejar acciones esperadas
            if (intent.action != null && intent.action != NOTIF_ACTION) return

            val eventId = intent.getIntExtra("eventId", 0)
            val title = intent.getStringExtra("title") ?: "Evento"
            val date = intent.getStringExtra("date") ?: ""
            val time = intent.getStringExtra("time") ?: ""

            Log.d("Calendario", "AlarmReceiver.onReceive: eventId=$eventId title=$title date=$date time=$time")

            ensureNotificationChannel(context)

            val notifManager = NotificationManagerCompat.from(context)

            // comprobar permiso runtime POST_NOTIFICATIONS (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                if (!granted) {
                    Log.w("AlarmReceiver", "POST_NOTIFICATIONS no concedido, no puedo mostrar notificación para eventId=$eventId")
                    return
                }
            }

            if (!notifManager.areNotificationsEnabled()) {
                Log.w("Calendario", "Notificaciones deshabilitadas para la app (areNotificationsEnabled=false)")
            }

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notif = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
                .setSmallIcon(com.example.myproyectofinal_din_carloscaramecerero.R.mipmap.ic_launcher)
                .setContentTitle("Recordatorio: $title")
                .setContentText("Fecha: $date ${if (time.isNotBlank()) "Hora: $time" else ""}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setAutoCancel(true)
                .build()

            try {
                notifManager.notify(eventId and 0x7fffffff, notif)
                Log.d("Calendario", "Notificación enviada: id=$eventId")
            } catch (ex: Exception) {
                Log.e("Calendario", "Error mostrando notificación", ex)
            }
        } catch (ex: Exception) {
            Log.e("Calendario", "Error en AlarmReceiver.onReceive", ex)
        }
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

    // Launcher para pedir permiso POST_NOTIFICATIONS en Android 13+
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (!granted) {
                // informar al usuario que sin permiso no recibirá notificaciones
                Toast.makeText(context, "Permiso de notificaciones no concedido; no se mostrarán recordatorios.", Toast.LENGTH_LONG).show()
            }
        }
    )

    // Cargar eventos al componer (desde AppRepository por usuario)
    LaunchedEffect(userEmail) {
        val loaded = AppRepository.loadEvents(context, userEmail)
        eventsMap.clear()
        eventsMap.addAll(loaded)
        ensureNotificationChannel(context)

        // comprobar permiso POST_NOTIFICATIONS en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val has = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!has) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

            loaded.forEach { ev -> scheduleAlarm(context, ev) }
    }

    // Guardar automáticamente cuando cambian los eventos
    androidx.compose.runtime.LaunchedEffect(Unit) {
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
        val now = java.time.LocalTime.now()
        fun showTimePicker() {
            TimePickerDialog(ctx, { _, hour, minute ->
                newEventTime = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
            }, now.hour, now.minute, true).show()
        }

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    if (newEventText.isNotBlank()) {
                        val newEv = CalendarEvent(
                            id = kotlin.random.Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
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
                    androidx.compose.material3.Text("Guardar")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showAddDialog = false
                    newEventText = ""
                    newEventTime = null
                }) {
                    androidx.compose.material3.Text("Cancelar")
                }
            },
            title = { androidx.compose.material3.Text("Nuevo evento") },
            text = {
                Column {
                    androidx.compose.material3.Text("Fecha: ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}")
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
