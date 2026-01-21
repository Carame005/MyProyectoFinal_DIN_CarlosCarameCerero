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
import androidx.compose.ui.platform.LocalContext
import com.example.myproyectofinal_din_carloscaramecerero.repository.AppRepository // <-- nuevo

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

/**
 * Pantalla de autenticación que soporta dos modos:
 *  - Registro: solicita nombre, correo, contraseña y confirmación.
 *  - Inicio de sesión: acepta usuario o correo + contraseña.
 *
 * Al registrarse guarda las credenciales y el perfil con [AppRepository].
 * Al iniciar sesión valida las credenciales existentes y carga/crea el perfil.
 *
 * También incluye un botón de purga para pruebas que borra todos los datos locales.
 *
 * @param onLogin Callback invocado con el [User] cuando la autenticación es correcta.
 */
@Composable
fun LoginScreen(
    onLogin: (User) -> Unit
) {
    val ctx = LocalContext.current

    // Modo: false = Iniciar sesión, true = Registro
    var isRegister by remember { mutableStateOf(false) }

    // Campos compartidos
    var name by remember { mutableStateOf("") }
    var identifier by remember { mutableStateOf("") } // puede ser email o usuario
    var password by remember { mutableStateOf("") }

    // Para registro: confirmar contraseña y admin flag
    var confirmPassword by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    // Errores
    var emailError by remember { mutableStateOf(false) }
    var passwordErrorMsg by remember { mutableStateOf("") }
    var generalErrorMsg by remember { mutableStateOf("") }

    // estados para purgar datos (confirmación y resultado)
    var showPurgeConfirm by remember { mutableStateOf(false) }
    var purgeResultMsg by remember { mutableStateOf("") }

    val emailIsValid = { s: String -> EMAIL_REGEX.matches(s) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBackground)
        ) {
            Column(modifier = Modifier
                .padding(20.dp)
            ) {
                Text(
                    text = if (isRegister) "Registro" else "Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isRegister) {
                    // Registro pide nombre, correo y contraseña (+ confirm)
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

                    OutlinedTextField(
                        value = identifier,
                        onValueChange = {
                            identifier = it
                            if (emailError && EMAIL_REGEX.matches(it)) emailError = false
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

                    if (emailError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Correo inválido", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
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

                    Spacer(modifier = Modifier.height(12.dp))

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

                    if (generalErrorMsg.isNotBlank()) {
                        Text(text = generalErrorMsg, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // Validaciones básicas
                                if (!emailIsValid(identifier)) {
                                    emailError = true
                                    generalErrorMsg = "Introduce un correo válido"
                                    return@Button
                                }
                                if (password.length < 4) {
                                    generalErrorMsg = "La contraseña debe tener al menos 4 caracteres"
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    generalErrorMsg = "Las contraseñas no coinciden"
                                    return@Button
                                }

                                // comprobar existencia
                                val existing = AppRepository.loadUser(ctx, identifier)
                                val creds = AppRepository.loadCredentials(ctx, identifier)
                                if (existing != null || creds != null) {
                                    generalErrorMsg = "Ya existe una cuenta con ese correo"
                                    return@Button
                                }

                                // crear y guardar usuario + credenciales
                                val user = User(
                                    name = name.ifBlank { identifier.substringBefore("@") },
                                    email = identifier,
                                    avatarRes = R.drawable.pfp,
                                    avatarUri = null,
                                    esAdmin = isAdmin
                                )
                                AppRepository.saveUser(ctx, user)
                                AppRepository.saveCredentials(ctx, identifier, password)

                                // iniciar sesión inmediatamente
                                onLogin(user)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AddButtonBlue)
                        ) {
                            Text(text = "Registrar", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = {
                        // cambiar a modo login
                        isRegister = false
                        generalErrorMsg = ""
                        emailError = false
                    }) {
                        Text("¿Ya tienes cuenta? Iniciar sesión", color = Color.White)
                    }
                } else {
                    // Modo INICIAR SESIÓN: identificador (usuario o correo) + contraseña
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Usuario o correo") },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    if (generalErrorMsg.isNotBlank()) {
                        Text(text = generalErrorMsg, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                generalErrorMsg = ""
                                // resolver identificador a email
                                val emailToCheck = if (identifier.contains("@")) {
                                    identifier
                                } else {
                                    // buscar por nombre
                                    AppRepository.findUserByName(ctx, identifier)?.email ?: identifier
                                }

                                val savedPw = AppRepository.loadCredentials(ctx, emailToCheck)
                                if (savedPw == null) {
                                    generalErrorMsg = "Usuario o correo no encontrado"
                                    return@Button
                                }
                                if (savedPw != password) {
                                    generalErrorMsg = "Contraseña incorrecta"
                                    return@Button
                                }

                                // cargar usuario si existe, si no crear uno mínimo
                                var user = AppRepository.loadUser(ctx, emailToCheck)
                                if (user == null) {
                                    user = User(
                                        name = identifier,
                                        email = emailToCheck,
                                        avatarRes = R.drawable.pfp,
                                        avatarUri = null,
                                        esAdmin = false
                                    )
                                    AppRepository.saveUser(ctx, user)
                                }
                                onLogin(user)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AddButtonBlue)
                        ) {
                            Text(text = "Entrar", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = {
                        isRegister = true
                        generalErrorMsg = ""
                        emailError = false
                    }) {
                        Text("¿No tienes cuenta? Registrarse", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón para purgar todos los datos/credenciales (solo pruebas)
                TextButton(onClick = { showPurgeConfirm = true }) {
                    Text("Purgar datos (pruebas)", color = Color.White)
                }

                // Mostrar mensaje de resultado tras purgar
                if (purgeResultMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = purgeResultMsg, color = Color.Green)
                }
            }
        }
    }

    // Diálogo de confirmación para purgar datos
    if (showPurgeConfirm) {
        AlertDialog(
            onDismissRequest = { showPurgeConfirm = false },
            title = { Text("Confirmar purga") },
            text = { Text("¿Desea eliminar todos los datos y credenciales de prueba? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showPurgeConfirm = false
                    try {
                        AppRepository.clearAllData(ctx)
                        purgeResultMsg = "Datos purgados correctamente"
                    } catch (_: Exception) {
                        purgeResultMsg = "Error al purgar datos"
                    }
                }) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}