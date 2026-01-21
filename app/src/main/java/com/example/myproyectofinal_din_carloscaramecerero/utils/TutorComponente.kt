package com.example.myproyectofinal_din_carloscaramecerero.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myproyectofinal_din_carloscaramecerero.R
import com.example.myproyectofinal_din_carloscaramecerero.model.User

/**
 * Componente reutilizable que muestra un usuario que puede ser añadido/quitado como tutorizado.
 *
 * Este componente encapsula la UI de la card (avatar, nombre, email, botón "Agregar/Eliminar") y
 * soporta estado expandible. Cuando está expandida muestra el contenido pasado en [expandedContent].
 *
 * Contrato (inputs/outputs):
 * - Inputs: `user: User`, `isAdded: Boolean`, `expanded: Boolean`, `modifier`
 * - Outputs: callbacks `onAdd`, `onRemove`, `onExpandChange`
 * - Side-effects: el componente no persiste datos por sí mismo; las acciones deben llamar al repo
 *   desde el contenedor (p. ej. `TutorPantalla`).
 *
 * Uso típico: en la pantalla de tutor, pasar `tasks/events` y callbacks para manipularlos como
 * contenido expandido.
 */
@Composable
fun TutorizadoCard(
    user: User,
    isAdded: Boolean,
    expanded: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true, // si false, ocultar botones Agregar/Eliminar
    expandedContent: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandChange(!expanded) }
            .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(modifier = Modifier.size(48.dp)) {
                    if (user.avatarUri != null) {
                        AsyncImage(model = user.avatarUri, contentDescription = "Avatar", modifier = Modifier.fillMaxSize())
                    } else if (user.avatarRes != 0) {
                        Image(painter = painterResource(user.avatarRes), contentDescription = "Avatar", modifier = Modifier.fillMaxSize())
                    } else {
                        Image(painter = painterResource(R.drawable.pfp), contentDescription = "Avatar", modifier = Modifier.fillMaxSize())
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                if (showActions) {
                    if (isAdded) {
                        TextButton(onClick = onRemove) { Text("Eliminado") }
                    } else {
                        TextButton(onClick = onAdd) { Text("Agregar") }
                    }
                }
            }

            // contenido expandido (delegado)
            if (expanded && expandedContent != null) {
                Spacer(modifier = Modifier.height(8.dp))
                expandedContent()
            }
        }
    }
}
