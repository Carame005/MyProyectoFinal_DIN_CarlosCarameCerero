package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.net.Uri
import android.view.View
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
    onPlayVideo: (uriString: String) -> Unit,
    canDeleteCollection: Boolean = true,
    canDeleteVideo: Boolean = true
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
                if (canDeleteCollection) {
                    IconButton(onClick = onDeleteCollection) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar colección", tint = Color(0xFFB00020))
                    }
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    collection.items.forEach { item ->
                        VideoItemCard(
                            video = item,
                            onDelete = { onDeleteVideo(item.id) },
                            onPlay = { onPlayVideo(item.uriString) },
                            canDelete = canDeleteVideo
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
    onPlay: () -> Unit,
    canDelete: Boolean = true
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
            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFB00020))
                }
            }
        }
    }
}

/**
 * Diálogo que reproduce un vídeo local dado su uriString.
 * Soporta modo pantalla completa mediante toggle.
 *
 * Ahora: si `uriString` comienza por http(s) se mostrará en un internal WebView;
 *       si es una URL de YouTube se intentará cargar la versión embebida.
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

    // helper: detectar y convertir URL de YouTube a la URL embebida
    fun youTubeEmbedUrl(url: String): String? {
        try {
            val lower = url.lowercase()
            if (lower.contains("youtube.com/watch?v=")) {
                val idx = lower.indexOf("v=")
                if (idx >= 0) {
                    val idPart = url.substring(idx + 2).split('&')[0]
                    // usar el dominio 'youtube-nocookie.com' para evitar algunos bloqueos de embedding
                    return "https://www.youtube-nocookie.com/embed/$idPart"
                }
            }
            if (lower.contains("youtu.be/")) {
                val parts = url.split("youtu.be/")
                if (parts.size > 1) {
                    val idPart = parts[1].split('?')[0]
                    // usar el dominio 'youtube-nocookie.com' para evitar algunos bloqueos de embedding
                    return "https://www.youtube-nocookie.com/embed/$idPart"
                }
            }
            if (lower.contains("youtube.com/shorts/")) {
                val parts = url.split("shorts/")
                if (parts.size > 1) {
                    val idPart = parts[1].split('?')[0]
                    // usar el dominio 'youtube-nocookie.com' para evitar algunos bloqueos de embedding
                    return "https://www.youtube-nocookie.com/embed/$idPart"
                }
            }
        } catch (_: Exception) { }
        return null
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = !isFullScreen)
    ) {
        // Cuando está en fullscreen ocupamos toda la pantalla, si no mostramos una card con altura adaptativa
        Box(
            modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 560.dp)
                    .padding(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                        // WebView path: usar embed para YouTube si es posible
                        val embed = youTubeEmbedUrl(uriString) ?: uriString
                        AndroidView(factory = { ctx ->
                            WebView(ctx).apply {
                                // Habilitar debugging en desarrollo
                                WebView.setWebContentsDebuggingEnabled(true)
                                // permitir cookies de terceros para embebidos
                                try { CookieManager.getInstance().setAcceptThirdPartyCookies(this, true) } catch (_: Exception) {}

                                // configuración segura para reproducir contenido embebido
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.javaScriptCanOpenWindowsAutomatically = true
                                settings.allowFileAccess = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                }

                                // Manejador de errores para evitar pantalla blanca y dar feedback
                                webViewClient = object : WebViewClient() {
                                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                        super.onReceivedError(view, errorCode, description, failingUrl)
                                        try {
                                            val errHtml = "<html><body style=\"color:#fff;background:#000;padding:16px\"><h3>Error al cargar el vídeo</h3><p>No se pudo cargar el contenido.</p></body></html>"
                                            loadDataWithBaseURL(null, errHtml, "text/html", "utf-8", null)
                                        } catch (_: Exception) {}
                                    }

                                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                        super.onReceivedError(view, request, error)
                                        try {
                                            val errHtml = "<html><body style=\"color:#fff;background:#000;padding:16px\"><h3>Error al cargar el vídeo</h3><p>No se pudo cargar el contenido.</p></body></html>"
                                            loadDataWithBaseURL(null, errHtml, "text/html", "utf-8", null)
                                        } catch (_: Exception) {}
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        try {
                                            // Si el contenido cargado no tiene altura, puede indicar bloqueo de frame
                                            if (view != null && view.contentHeight <= 0) {
                                                // reintentar después de un breve retraso (algunos embeds tardan)
                                                view.postDelayed({
                                                    try {
                                                        if (view.contentHeight <= 0) {
                                                            // fallback: abrir externamente
                                                            try {
                                                                val i = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(embed)).apply { addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK) }
                                                                ctx.startActivity(i)
                                                            } catch (_: Exception) { /* ignore */ }
                                                        }
                                                    } catch (_: Exception) {}
                                                }, 700)
                                            }
                                        } catch (_: Exception) {}
                                    }
                                }

                                // WebChromeClient para gestionar permisos (audio/video/autoplay) y UI del WebView
                                webChromeClient = object : WebChromeClient() {
                                    override fun onPermissionRequest(request: PermissionRequest) {
                                        try {
                                            // No conceder recursos de captura (audio/video) desde el WebView porque la app no usa input de usuario
                                            val deniedCapture = arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE, PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                                            val toGrant = request.resources.filter { res ->
                                                !deniedCapture.contains(res)
                                            }.toTypedArray()

                                            if (toGrant.isNotEmpty()) {
                                                request.grant(toGrant)
                                            } else {
                                                // Si sólo piden captura, denegar para evitar que Chromium solicite permisos Android a nivel de recording
                                                request.deny()
                                            }
                                        } catch (_: Exception) {
                                            try { request.deny() } catch (_: Exception) {}
                                        }
                                    }

                                    override fun onConsoleMessage(message: android.webkit.ConsoleMessage?): Boolean {
                                        try {
                                            val msg = message?.message() ?: ""
                                            // detectar bloqueo por X-Frame-Options u otros indicativos en consola
                                            if (msg.contains("Refused to display") || msg.contains("blocked by X-Frame-Options") || msg.contains("Allow-FRAME-ANCESTOR") || msg.contains("content is blocked")) {
                                                try {
                                                    val i = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(embed)).apply { addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK) }
                                                    ctx.startActivity(i)
                                                } catch (_: Exception) { /* ignore */ }
                                            }
                                        } catch (_: Exception) {}
                                        return super.onConsoleMessage(message)
                                    }
                                }

                                // usar iframe embed cuando sea YouTube para evitar problemas de redirección
                                try {
                                    if (embed.startsWith("https://www.youtube.com/embed/")) {
                                        val html = "<html><body style=\"margin:0;padding:0;background:black;\">" +
                                            "<iframe width=\"100%\" height=\"100%\" src=\"${embed}?autoplay=1&mute=1&modestbranding=1&playsinline=1\" " +
                                            "frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>"
                                        // load HTML with base URL to allow relative resources
                                        loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
                                    } else {
                                        // para otras URLs HTTP(S) cargar directamente
                                        loadUrl(embed)
                                    }
                                } catch (ex: Exception) {
                                    // fallback a loadUrl
                                    try { loadUrl(embed) } catch (_: Exception) {}
                                }
                                // asegurar hardware acceleration si está disponible
                                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                            }
                        }, modifier = if (isFullScreen) Modifier.fillMaxSize() else Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 420.dp))
                    } else {
                        // VideoView para URIs locales/content
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
                                .heightIn(min = 180.dp, max = 420.dp)
                        )
                    }

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
