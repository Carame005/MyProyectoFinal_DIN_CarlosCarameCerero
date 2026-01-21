package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.example.myproyectofinal_din_carloscaramecerero.model.TimeBreakdown
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.VideoCollection
import com.example.myproyectofinal_din_carloscaramecerero.pantallas.VideoItem

/**
 * Elemento de progreso simple que muestra una barra coloreada y texto.
 */
@Composable
fun TimeProgressItem(
    label: String,
    value: Long,
    maxValue: Long,
    color: androidx.compose.ui.graphics.Color
) {
    val progress = (value.toFloat() / maxValue.coerceAtLeast(1)).coerceIn(0f, 1f)

    Column {
        Text(
            text = "$value $label",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(12.dp))
            )
        }
    }
}

/**
 * Gráfico de progreso temporal compuesto por varios TimeProgressItem.
 */
@Composable
fun TimeProgressChart(time: com.example.myproyectofinal_din_carloscaramecerero.model.TimeBreakdown) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TimeProgressItem("años", time.years, 10, Color(0xFF2ECC71))
        TimeProgressItem("meses", time.months, 12, Color(0xFF27AE60))
        TimeProgressItem("días", time.days, 30, Color(0xFF3498DB))
        TimeProgressItem("horas", time.hours, 24, Color(0xFF5DADE2))
    }
}

/**
 * Tarjeta que representa una colección de vídeos. Puede expandirse para listar items
 * y ofrece botones para reproducir, eliminar y añadir vídeos.
 */
@Composable
fun CollectionCard(
    collection: com.example.myproyectofinal_din_carloscaramecerero.pantallas.VideoCollection,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onAddVideo: () -> Unit,
    onDeleteCollection: () -> Unit,
    onDeleteVideo: (videoId: Int) -> Unit,
    onPlayVideo: (uriString: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = collection.title,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onToggleExpanded() }
                )
                IconButton(onClick = onDeleteCollection) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar colección", tint = Color(0xFFB00020))
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    collection.items.forEach { item ->
                        VideoItemCard(
                            video = item,
                            onDelete = { onDeleteVideo(item.id) },
                            onPlay = { onPlayVideo(item.uriString) }
                        )
                    }

                    // botón pequeño añadir vídeo dentro de la colección
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D47A1))
                                .clickable { onAddVideo() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Añadir vídeo", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card que muestra metadatos de un vídeo dentro de una colección y acciones (reproducir, borrar).
 */
@Composable
fun VideoItemCard(
    video: com.example.myproyectofinal_din_carloscaramecerero.pantallas.VideoItem,
    onDelete: () -> Unit,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = video.title)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = video.description, color = Color.Gray)
            }
            IconButton(onClick = onPlay) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Reproducir")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFB00020))
            }
        }
    }
}

/**
 * Diálogo que reproduce un vídeo local dado su uriString.
 * Soporta modo pantalla completa mediante toggle.
 *
 * @param uriString Uri en formato String (se parsea a Uri internamente).
 * @param onClose Callback para cerrar el diálogo/reproductor.
 */
@Composable
fun VideoPlayerDialog(
    uriString: String,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var isFullScreen by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = !isFullScreen)
    ) {
        // Cuando está en fullscreen ocupamos toda la pantalla, si no mostramos una card con altura fija
        Box(
            modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier.wrapContentSize()
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // VideoView: tamaño variable según fullscreen
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val mc = MediaController(ctx)
                                mc.setAnchorView(this)
                                setMediaController(mc)
                                try {
                                    val u = Uri.parse(uriString)
                                    setVideoURI(u)
                                    requestFocus()
                                    start()
                                } catch (ex: Exception) {
                                    // ignore; mostrar vacío si falla
                                }
                            }
                        },
                        modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )

                    // Controles superiores: fullscreen toggle y cerrar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { isFullScreen = !isFullScreen }) {
                            Icon(
                                imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullScreen) "Salir de pantalla completa" else "Pantalla completa",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = {
                            onClose()
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                        }
                    }

                    // En modo normal mostramos botón de cerrar en la parte inferior como antes
                    if (!isFullScreen) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = onClose) { Text("Cerrar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
