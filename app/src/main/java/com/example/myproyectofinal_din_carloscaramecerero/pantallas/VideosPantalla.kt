package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myproyectofinal_din_carloscaramecerero.utils.CollectionCard
import com.example.myproyectofinal_din_carloscaramecerero.utils.VideoPlayerDialog
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.random.Random
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.core.content.edit

private const val PREFS_NAME_COLLECTIONS = "video_collections_prefs"
private const val COLLECTIONS_KEY = "video_collections_serialized"

/**
 * Data models locales (simples)
 */
data class VideoItem(
    val id: Int,
    val title: String,
    val description: String,
    val uriString: String
)

data class VideoCollection(
    val id: Int,
    val title: String,
    val items: List<VideoItem>
)

@Composable
fun StatsListScreen() {
    val context = LocalContext.current
    var collections by remember { mutableStateOf(listOf<VideoCollection>()) }

    var showAddCollectionDialog by remember { mutableStateOf(false) }
    var showAddVideoForCollectionId by remember { mutableStateOf<Int?>(null) }
    var playingUri by remember { mutableStateOf<String?>(null) }

    // estado temporal para la uri seleccionada en el diálogo
    var pendingVideoUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para seleccionar vídeo desde galería/archivos (mimetype video/*)
    val pickVideoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                // solicitar permiso persistente para poder reproducir tras cambios de proceso
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (_: Exception) {
                // ignore
            }
            pendingVideoUri = uri
        }
    }

    // Cargar colecciones al componer
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(PREFS_NAME_COLLECTIONS, Context.MODE_PRIVATE)
        collections = deserializeCollections(prefs.getString(COLLECTIONS_KEY, null))
    }

    // Guardar cada vez que collections cambian
    LaunchedEffect(collections) {
        val prefs = context.getSharedPreferences(PREFS_NAME_COLLECTIONS, Context.MODE_PRIVATE)
        prefs.edit { putString(COLLECTIONS_KEY, serializeCollections(collections)) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .padding(bottom = 96.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(items = collections, key = { it.id }) { collection ->
                    var expandedLocal by remember { mutableStateOf(false) }

                    CollectionCard(
                        collection = collection,
                        expanded = expandedLocal,
                        onToggleExpanded = { expandedLocal = !expandedLocal },
                        onAddVideo = { showAddVideoForCollectionId = collection.id },
                        onDeleteCollection = {
                            collections = collections.filterNot { it.id == collection.id }
                        },
                        onDeleteVideo = { videoId ->
                            collections = collections.map {
                                if (it.id == collection.id) it.copy(items = it.items.filterNot { v -> v.id == videoId })
                                else it
                            }
                        },
                        onPlayVideo = { uriStr ->
                            playingUri = uriStr
                        }
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
                    .background(Color(0xFF0D47A1))
                    .clickable { showAddCollectionDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir colección",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (showAddCollectionDialog) {
        AddCollectionDialog(
            onDismiss = { showAddCollectionDialog = false },
            onCollectionAdded = { title ->
                val new = VideoCollection(
                    id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                    title = title.ifBlank { "Colección ${Random.nextInt(1000)}" },
                    items = emptyList()
                )
                collections = collections + new
                showAddCollectionDialog = false
            }
        )
    }

    showAddVideoForCollectionId?.let { colId ->
        AddVideoDialog(
            onDismiss = {
                showAddVideoForCollectionId = null
                pendingVideoUri = null
            },
            onPickVideo = {
                // lanzar el selector; OpenDocument requiere array de mime types
                pickVideoLauncher.launch(arrayOf("video/*"))
            },
            selectedUri = pendingVideoUri,
            onVideoAdded = { title, desc, uriStr ->
                val newItem = VideoItem(
                    id = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                    title = title.ifBlank { "Vídeo ${Random.nextInt(1000)}" },
                    description = desc,
                    uriString = uriStr
                )
                collections = collections.map {
                    if (it.id == colId) it.copy(items = it.items + newItem) else it
                }
                showAddVideoForCollectionId = null
                pendingVideoUri = null
            }
        )
    }

    playingUri?.let { uriStr ->
        VideoPlayerDialog(
            uriString = uriStr,
            onClose = { playingUri = null }
        )
    }
}

@Composable
fun AddCollectionDialog(
    onDismiss: () -> Unit,
    onCollectionAdded: (String) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva colección de vídeos") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") }
                )
                Spacer(Modifier.height(8.dp))
                Text("Cada colección agrupa varios vídeos que podrá reproducir fácilmente.")
            }
        },
        confirmButton = {
            TextButton(onClick = { onCollectionAdded(title.text) }) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddVideoDialog(
    onDismiss: () -> Unit,
    onPickVideo: () -> Unit,
    selectedUri: Uri?,
    onVideoAdded: (title: String, description: String, uriString: String) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var desc by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir vídeo") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
                Spacer(Modifier.height(8.dp))

                // botón para seleccionar vídeo desde archivos/galería
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onPickVideo) {
                        Text("Seleccionar vídeo")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // mostrar uri o nombre si hay seleccionado
                    Text(text = selectedUri?.lastPathSegment ?: (selectedUri?.toString() ?: "Ninguno seleccionado"))
                }

                Spacer(Modifier.height(6.dp))
                Text("Se recomienda seleccionar el vídeo desde la galería o archivos. Si desea, puede dejar la URI vacía.")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val uriStr = selectedUri?.toString() ?: ""
                onVideoAdded(title.text, desc.text, uriStr)
            }) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// Serialización sencilla y robusta con URLEncoder/URLDecoder
private fun serializeCollections(list: List<VideoCollection>): String {
    return list.joinToString("###") { col ->
        val titleEnc = URLEncoder.encode(col.title, "UTF-8")
        val itemsPart = col.items.joinToString(";;") { item ->
            val t = URLEncoder.encode(item.title, "UTF-8")
            val d = URLEncoder.encode(item.description, "UTF-8")
            val u = URLEncoder.encode(item.uriString, "UTF-8")
            "${item.id}||$t||$d||$u"
        }
        "${col.id}::${titleEnc}::${itemsPart}"
    }
}

private fun deserializeCollections(serialized: String?): List<VideoCollection> {
    if (serialized.isNullOrEmpty()) return emptyList()
    return try {
        serialized.split("###").mapNotNull { entry ->
            val parts = entry.split("::")
            if (parts.size < 3) return@mapNotNull null
            val id = parts[0].toIntOrNull() ?: return@mapNotNull null
            val title = URLDecoder.decode(parts[1], "UTF-8")
            val itemsRaw = parts[2]
            val items = if (itemsRaw.isEmpty()) emptyList() else {
                itemsRaw.split(";;").mapNotNull { it2 ->
                    val p = it2.split("||")
                    if (p.size < 4) return@mapNotNull null
                    val vid = p[0].toIntOrNull() ?: return@mapNotNull null
                    val t = URLDecoder.decode(p[1], "UTF-8")
                    val d = URLDecoder.decode(p[2], "UTF-8")
                    val u = URLDecoder.decode(p[3], "UTF-8")
                    VideoItem(id = vid, title = t, description = d, uriString = u)
                }
            }
            VideoCollection(id = id, title = title, items = items)
        }
    } catch (_: Exception) {
        emptyList()
    }
}
