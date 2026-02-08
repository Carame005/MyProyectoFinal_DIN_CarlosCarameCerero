package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myproyectofinal_din_carloscaramecerero.model.ReportSummary
import com.example.myproyectofinal_din_carloscaramecerero.model.ReportFilters
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp

/**
 * Componente que dibuja un gráfico de barras horizontales según los datos del resumen.
 * Ahora acepta opcionalmente `filters` para mostrar únicamente las series seleccionadas.
 */
@Composable
fun ReportChart(summary: ReportSummary, filters: ReportFilters? = null, modifier: Modifier = Modifier) {
    // resolver colores del tema
    val cs = MaterialTheme.colorScheme
    val baseColors = listOf(
        cs.primary,
        cs.secondary,
        cs.primaryContainer,
        cs.secondaryContainer,
        cs.surfaceVariant
    )

    // Construir lista de entradas según filtros (si filters == null mostramos todas)
    val entries = mutableListOf<Pair<String, Float>>()
    if (filters == null || filters.includeCompleted) entries.add("Completadas" to summary.tasksCompleted.toFloat())
    if (filters == null || filters.includeInProgress) entries.add("En Progreso" to summary.tasksInProgress.toFloat())
    if (filters == null || filters.includePending) entries.add("Pendientes" to summary.tasksPending.toFloat())
    if (filters == null || filters.includeEvents) entries.add("Eventos" to summary.eventsCount.toFloat())
    if (filters == null || filters.includeVideos) entries.add("Vídeos" to summary.totalVideos.toFloat())

    // Si no hay entradas seleccionadas, mostrar mensaje
    if (entries.isEmpty()) {
        Column(modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF35343A))
            .padding(12.dp)
        ) {
            Text("No hay datos seleccionados para mostrar.", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    val values = entries.map { it.second }
    val labels = entries.map { it.first }

    // Determinar máximo para escalar las barras
    val maxVal = values.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    val cardGray = Color(0xFF35343A)

    Column(modifier = modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(cardGray)
        .padding(8.dp)
    ) {
        for (i in values.indices) {
            val color = baseColors.getOrElse(i) { cs.primary }
            ChartRow(label = labels[i], value = values[i], maxVal = maxVal, color = color, cardGray = cardGray)
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun ChartRow(label: String, value: Float, maxVal: Float, color: Color, cardGray: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, modifier = Modifier.weight(0.5f), maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
        Box(modifier = Modifier
            .weight(0.5f)
            .height(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(cardGray)
        ) {
            val bg = cardGray
            val filledWidthRatio = (value / maxVal).coerceIn(0f, 1f)

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = bg, topLeft = Offset.Zero, size = Size(size.width, size.height))
                val width = size.width * filledWidthRatio
                drawRect(color = color, topLeft = Offset(0f, 0f), size = Size(width, size.height))
            }
        }
    }
}
