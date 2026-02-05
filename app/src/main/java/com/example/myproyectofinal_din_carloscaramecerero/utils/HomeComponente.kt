package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tarjeta resumen presionable usada en la pantalla Home.
 *
 * - icon: composable que dibuja el icono de la tarjeta.
 * - title: texto descriptivo.
 * - value: valor principal mostrado.
 * - modifier: modifier pasado desde el llamador (no aplicar weight aquí dentro).
 * - onClick: acción cuando se pulsa la tarjeta (por ejemplo expandir sección).
 */
@Composable
fun SummaryCard(
    icon: @Composable () -> Unit,
    title: String,
    value: String,
    modifier: Modifier = Modifier, // <-- aceptar modifier desde el llamador
    onClick: () -> Unit = {} // nuevo callback para hacerla presionable
) {
    Card(
        modifier = modifier
            .fillMaxWidth() // ocupar todo el ancho; altura indefinida para que haga wrap
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D47A1)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                // permitir que el título haga wrap en varias líneas (no limitar maxLines)
                Text(text = title, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}