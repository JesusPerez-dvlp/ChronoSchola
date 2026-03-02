package com.example.chronoschola.ui

import androidx.compose.ui.graphics.Color
import com.example.chronoschola.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val listaFondosCard = listOf(
    R.drawable.card1,
    R.drawable.card2,
    R.drawable.card3,
    R.drawable.card4,
    R.drawable.card5,
    R.drawable.card6
)

val listaAvatares = listOf(
    R.drawable.usuario,
    R.drawable.usuario,
    R.drawable.usuario,
    R.drawable.usuario,
    R.drawable.usuario,
    R.drawable.usuario
)

val listaColoresAvisos = listOf(
    Color(0xFFFFF9C4),
    Color(0xFFBBDEFB), // Azul pastel
    Color(0xFFC8E6C9), // Verde pastel
    Color(0xFFFFCCBC), // Naranja suave
    Color(0xFFE1BEE7), // Lila
    Color(0xFFB2EBF2)  // Cian suave
)

fun obtenerFechaActual(): String {
    val fecha = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "ES"))
    return fecha.format(formatter).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}