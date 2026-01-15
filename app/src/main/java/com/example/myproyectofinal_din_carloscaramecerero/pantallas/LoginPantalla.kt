package com.example.myproyectofinal_din_carloscaramecerero.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myproyectofinal_din_carloscaramecerero.model.User
import com.example.myproyectofinal_din_carloscaramecerero.R
import com.example.myproyectofinal_din_carloscaramecerero.utils.DarkBackground
import com.example.myproyectofinal_din_carloscaramecerero.utils.AddButtonBlue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

@Composable
fun LoginScreen(
    onLogin: (User) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) } // switch en vez de slider

    // NUEVO: estado para error de email
    var emailError by remember { mutableStateOf(false) }
    val emailErrorMsg = "Correo inválido"

    // ...existing UI setup...
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            // ...existing card props...
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBackground) // mismo color oscuro
        ) {
            Column(modifier = Modifier
                .padding(20.dp)
            ) {
                Text(text = "Iniciar sesión", style = MaterialTheme.typography.titleLarge, color = Color.White)

                Spacer(modifier = Modifier.height(12.dp))

                // ...existing nombre field...
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AddButtonBlue,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedLabelColor = AddButtonBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // CAMBIO: campo email con isError y limpieza de error en onValueChange
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError && EMAIL_REGEX.matches(it)) {
                            emailError = false
                        }
                    },
                    label = { Text("Correo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AddButtonBlue,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedLabelColor = AddButtonBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f)
                    )
                )

                // NUEVO: mensaje de error visible
                if (emailError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = emailErrorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ...existing password field...
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AddButtonBlue,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedLabelColor = AddButtonBlue,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f)
                    )
                )

                // ...existing switch and spacing...

                Spacer(modifier = Modifier.height(12.dp))

                // Switch en lugar de Slider, con la paleta del botón añadir tarea
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Administrador (temporal): ${if (isAdmin) "Sí" else "No"}",
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isAdmin,
                        onCheckedChange = { isAdmin = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AddButtonBlue,
                            uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                            uncheckedTrackColor = Color.Gray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Entrar con validación por regex
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // VALIDACIÓN: usar regex antes de proceder
                            if (!EMAIL_REGEX.matches(email)) {
                                emailError = true
                                return@Button
                            }

                            // crear user con avatar placeholder y flag admin según switch
                            val user = User(
                                name = name.ifBlank { "Usuario" },
                                email = email.ifBlank { "usuario@correo.com" },
                                avatarRes = R.drawable.pfp,
                                avatarUri = null,
                                esAdmin = isAdmin
                            )
                            onLogin(user)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AddButtonBlue)
                    ) {
                        Text(text = "Entrar", color = Color.White)
                    }
                }
            }
        }
    }
}