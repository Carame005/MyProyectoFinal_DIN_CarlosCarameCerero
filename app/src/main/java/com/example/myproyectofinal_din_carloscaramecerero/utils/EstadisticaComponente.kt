package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.myproyectofinal_din_carloscaramecerero.model.TimeBreakdown

@Composable
fun TimeProgressItem(
    label: String,
    value: Long,
    maxValue: Long,
    color: Color
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

@Composable
fun TimeProgressChart(time: TimeBreakdown) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TimeProgressItem("años", time.years, 10, Color(0xFF2ECC71))
        TimeProgressItem("meses", time.months, 12, Color(0xFF27AE60))
        TimeProgressItem("días", time.days, 30, Color(0xFF3498DB))
        TimeProgressItem("horas", time.hours, 24, Color(0xFF5DADE2))
    }
}
