package com.example.chronoschola.model

import androidx.compose.ui.graphics.Color

data class Materia(
    val id: String = "",
    val nombre: String,
    val horario: String,
    val color: Color = Color.Gray,
    val codigo: String,
    val fondoIndex: Int = 0
)