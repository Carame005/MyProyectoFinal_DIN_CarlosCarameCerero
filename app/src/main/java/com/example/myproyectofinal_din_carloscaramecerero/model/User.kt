package com.example.myproyectofinal_din_carloscaramecerero.model

import android.net.Uri

data class User(
    val name: String,
    val email: String,
    val avatarRes: Int,
    val avatarUri: Uri? = null,
    var esAdmin : Boolean = false
)
