package com.example.myproyectofinal_din_carloscaramecerero.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val status: TaskStatus
)