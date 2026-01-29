package com.example.myproyectofinal_din_carloscaramecerero.utils

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import com.example.myproyectofinal_din_carloscaramecerero.R
import androidx.compose.ui.platform.LocalContext
import android.util.Log

/**
 * Usuario por defecto usado cuando no hay perfil cargado.
 */
val defaultUser = User(
    name = "Usuario",
    email = "usuario@correo.com",
    avatarRes = R.drawable.pfp
)

/**
 * Menú de perfil desplegable que permite cambiar avatar mediante selector del sistema.
 *
 * @param user Usuario mostrado.
 * @param expanded Estado del DropdownMenu.
 * @param onDismiss Cierra el menú.
 * @param onAvatarChange Callback con la Uri seleccionada para actualizar el avatar.
 */
@Composable
fun ProfileMenu(
    user: User,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onAvatarChange: (Uri) -> Unit
) {
    val ctx = LocalContext.current

    // Usar OpenDocument para poder pedir permisos persistentes sobre la URI
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // solicitar permiso persistente para poder usar la URI tras reinicios
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                ctx.contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (ex: Exception) {
                Log.e("ProfileMenu", "takePersistableUriPermission failed", ex)
            }
            onAvatarChange(it)
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable {
                        // lanzar OpenDocument para imágenes
                        imagePicker.launch(arrayOf("image/*"))
                    }
            ) {
                if (user.avatarUri != null) {
                    AsyncImage(
                        model = user.avatarUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(user.avatarRes),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
