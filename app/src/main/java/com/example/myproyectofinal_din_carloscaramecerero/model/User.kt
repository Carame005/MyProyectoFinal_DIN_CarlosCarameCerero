package com.example.myproyectofinal_din_carloscaramecerero.model

import android.net.Uri

/**
 * Representa un perfil de usuario de la aplicación.
 *
 * @property name Nombre mostrado.
 * @property email Identificador único (se usa para diferenciar datos por perfil).
 * @property avatarRes Recurso drawable por defecto para avatar.
 * @property avatarUri Uri opcional a un avatar personalizado.
 * @property esAdmin Marca si el usuario tiene privilegios administrativos (habilita opciones adicionales).
 */
data class User(
    val name: String,
    val email: String,
    val avatarRes: Int,
    val avatarUri: Uri? = null,
    var esAdmin : Boolean = false
)
