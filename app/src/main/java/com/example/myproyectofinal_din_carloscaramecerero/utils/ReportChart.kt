package com.example.myproyectofinal_din_carloscaramecerero.utils

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.luminance
import java.util.Locale

/**
 * Componente que dibuja un gráfico de barras horizontales según los datos del resumen.
 * Ahora acepta opcionalmente `filters` para mostrar únicamente las series seleccionadas.
 */
@Suppress("ModifierParameter")
@Composable
fun ReportChart(summary: ReportSummary, filters: ReportFilters? = null, isLightFilter: Boolean = false, modifier: Modifier = Modifier) {
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
        val cardGrayEmpty = if (isLightFilter) Color(0xFFE6E1E8) else Color(0xFF35343A)
        Column(modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cardGrayEmpty)
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

    // Calcular ratios crudos
    val rawRatios = values.map { v -> if (maxVal > 0f) (v / maxVal).coerceAtLeast(0f) else 0f }

    // Aplicar mínimo visual y redistribución para que no se pierdan barras pequeñas
    val minVisibleRatio = 0.06f // 6% mínimo visual para valores > 0
    val visualRatios = MutableList(rawRatios.size) { 0f }

    // Inicializar
    for (i in rawRatios.indices) {
        visualRatios[i] = if (values[i] <= 0f) 0f else maxOf(rawRatios[i], minVisibleRatio)
    }

    var sumVisual = visualRatios.sum()

    // Si la suma excede 1.0, reducimos proporcionalmente pero sin bajar de minVisibleRatio
    if (sumVisual > 1f) {
        // Ajuste iterativo: reducimos el exceso repartiendo la reducción entre los que están por encima del mínimo
        var excess = sumVisual - 1f
        val eps = 1e-6f
        val mutable = visualRatios.toMutableList()
        var adjustableIndices = mutable.indices.filter { idx -> mutable[idx] > minVisibleRatio }

        while (excess > eps && adjustableIndices.isNotEmpty()) {
            val sumAdjustable = adjustableIndices.sumOf { idx -> (mutable[idx] - minVisibleRatio).toDouble() }.toFloat()
            if (sumAdjustable <= 0f) break
            // Reducir cada ajustable en proporción a su margen
            for (idx in adjustableIndices) {
                val margin = mutable[idx] - minVisibleRatio
                val reduce = margin * (excess / sumAdjustable)
                mutable[idx] = (mutable[idx] - reduce).coerceAtLeast(minVisibleRatio)
            }
            sumVisual = mutable.sum()
            excess = (sumVisual - 1f).coerceAtLeast(0f)
            adjustableIndices = mutable.indices.filter { idx -> mutable[idx] > minVisibleRatio }
        }

        // Si todavía hay exceso (ej. nPos * minVisibleRatio > 1), normalizamos todo para que sumen 1
        if (sumVisual > 1f + eps) {
            val factor = 1f / sumVisual
            for (i in mutable.indices) mutable[i] = mutable[i] * factor
        }

        // Copiar de vuelta
        for (i in mutable.indices) visualRatios[i] = mutable[i]
        // sumVisual recalculado anteriormente; no es necesario reasignarlo aquí
    }

    val cardGray = if (isLightFilter) Color(0xFFE6E1E8) else Color(0xFF35343A)
    val barBackground = if (isLightFilter) Color(0xFFDCD6DE) else Color(0xFF2E2D31) // fondo interior de la barra (varía con el filtro)

    Column(modifier = modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(cardGray)
        .padding(12.dp)
    ) {

        for (i in values.indices) {
            val label = labels[i]
            val color = when (label) {
                "En Progreso" -> Color(0xFF0D47A1) // azul oscuro contrastado para mostrar número en blanco
                else -> baseColors.getOrElse(i) { cs.primary }
            }
            val filledRatio = visualRatios[i].coerceIn(0f, 1f)
            ChartRow(label = labels[i], value = values[i], filledWidthRatio = filledRatio, color = color, barBackground = barBackground)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ChartRow(label: String, value: Float, filledWidthRatio: Float, color: Color, barBackground: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        // Etiqueta: darle algo más de espacio para que no se corte
        Text(
            text = label,
            modifier = Modifier.weight(0.42f).padding(end = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(modifier = Modifier
            .weight(0.58f)
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(barBackground)
        ) {
            // Barra rellenada
            Box(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(filledWidthRatio)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
            )

            // Valor numérico: colocarlo al final de la barra rellena pero legible
            val valueText = if (value % 1.0f == 0f) value.toInt().toString() else String.format(Locale.getDefault(), "%.1f", value)
            val contrastColor = if (color.luminance() < 0.5f) Color.White else Color.Black

            Box(modifier = Modifier.fillMaxSize()) {
                // Si la barra rellena es muy pequeña, mostramos el texto en el borde derecho sobre el fondo de la barra
                if (filledWidthRatio > 0.18f) {
                    // texto dentro del área rellena (alineado a la derecha)
                    Text(
                        text = valueText,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp),
                        color = contrastColor,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                } else {
                    // texto fuera (a la derecha) para no superponerse con la barra pequeña
                    Text(
                        text = valueText,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 6.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
