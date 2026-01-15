package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    events: List<Pair<LocalDate, String>>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    // Días de la semana (empezando en domingo)
    val weekDays = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp), // quitar separación extra
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        for (d in weekDays) {
            Text(
                text = d,
                modifier = Modifier.weight(1f),
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        }
    }

    // Construir lista de fechas (con nulls para huecos)
    val firstOfMonth = currentMonth.atDay(1)
    val monthLength = currentMonth.lengthOfMonth()
    val startOffset = (firstOfMonth.dayOfWeek.value % 7) // 0 si Sunday, 1 si Monday, etc.
    val totalCells = startOffset + monthLength
    val cells = (0 until totalCells).map { idx ->
        val dayIndex = idx - startOffset + 1
        if (dayIndex in 1..monthLength) currentMonth.atDay(dayIndex) else null
    }

    // Aplicar spacing y contentPadding mínimos para que el grid encaje en la altura fija de la pantalla
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        // nota: no usamos spacing demasiado grande para evitar empujar hacia abajo la lista de eventos
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(2.dp)
    ) {
        items(cells) { date ->
            if (date == null) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .aspectRatio(1f)
                )
            } else {
                DayCell(
                    date = date,
                    isToday = date == today,
                    isSelected = date == selectedDate,
                    hasEvents = events.any { it.first == date },
                    onClick = { onDateSelected(date) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayCell(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    hasEvents: Boolean,
    onClick: () -> Unit
) {
    val background = when {
        isSelected -> PrimaryBlue
        isToday -> SurfaceGray
        else -> Color.Transparent
    }
    val textColor = if (isSelected) Color.White else Color.Unspecified

    Box(
        modifier = Modifier
            .padding(2.dp) // reducido para compactar
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = date.dayOfMonth.toString(), color = textColor)
            if (hasEvents) {
                Spacer(modifier = Modifier.size(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color = AddButtonBlue, shape = MaterialTheme.shapes.small)
                )
            }
        }
    }
}
