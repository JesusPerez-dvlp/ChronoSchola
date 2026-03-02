package com.example.chronoschola.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.chronoschola.R

@Composable
fun ContenidoPantallaConfiguracion(navController: NavController) {
    var notificacionesActivadas by remember { mutableStateOf(true) }
    var modoOscuro by remember { mutableStateOf(false) }
    var mostrarDialogoPass by remember { mutableStateOf(false) }
    var nuevaPass by remember { mutableStateOf("") }
    var confirmarPass by remember { mutableStateOf("") }
    var mensajeResultado by remember { mutableStateOf("") }
    var mostrarDialogoAyuda by remember { mutableStateOf(false) }
    var mostrarAlertaResultado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
            }
            Text("Configuración", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "General",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Notificaciones", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Recibir alertas de clases y tareas",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Switch(
                checked = notificacionesActivadas,
                onCheckedChange = { notificacionesActivadas = it }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Cambiar apariencia de la app",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Switch(
                checked = modoOscuro,
                onCheckedChange = { modoOscuro = it }
            )
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Text(
            "Cuenta",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mostrarDialogoPass = true }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Cambiar Contraseña", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mostrarDialogoAyuda = true }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Ayuda y Soporte", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "ChronoSchola v1.0",
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (mostrarDialogoPass) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoPass = false },
                title = { Text("Nueva Contraseña") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nuevaPass,
                            onValueChange = { nuevaPass = it },
                            label = { Text("Nueva contraseña") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmarPass,
                            onValueChange = { confirmarPass = it },
                            label = { Text("Confirmar contraseña") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevaPass.length >= 6 && nuevaPass == confirmarPass) {
                                val user = FirebaseAuth.getInstance().currentUser
                                user?.updatePassword(nuevaPass)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            mensajeResultado =
                                                "Contraseña actualizada correctamente"
                                            mostrarDialogoPass = false
                                            mostrarAlertaResultado = true
                                            nuevaPass = ""
                                            confirmarPass = ""
                                        } else {
                                            mensajeResultado =
                                                "Error: ${task.exception?.message}. Intente cerrar sesión y volver a entrar."
                                            mostrarAlertaResultado = true
                                        }
                                    }
                            } else {
                                mensajeResultado = "Las contraseñas no coinciden o son muy cortas"
                                mostrarAlertaResultado = true
                            }
                        }
                    ) { Text("Actualizar") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoPass = false }) { Text("Cancelar") }
                }
            )
        }
        if (mostrarAlertaResultado) {
            AlertDialog(
                onDismissRequest = { mostrarAlertaResultado = false },
                title = { Text("Aviso") },
                text = { Text(mensajeResultado) },
                confirmButton = {
                    TextButton(onClick = { mostrarAlertaResultado = false }) { Text("OK") }
                }
            )
        }
        if (mostrarDialogoAyuda) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoAyuda = false },
                icon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
                title = { Text("Centro de Ayuda") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text("Preguntas Frecuentes", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        PreguntaRespuesta(
                            "¿Cómo me uno a una clase?",
                            "Pide el código único (ej: MAT-123) a tu profesor e ingrésalo en el botón '+' del inicio."
                        )
                        PreguntaRespuesta(
                            "¿Cómo elimino una clase?",
                            "Entra a la materia, toca el ícono de engranaje ⚙️ arriba a la derecha y selecciona la opción de salir o eliminar."
                        )
                        PreguntaRespuesta(
                            "No veo mis avisos",
                            "Asegúrate de tener conexión a internet. Los avisos se actualizan en tiempo real."
                        )
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        Text("Soporte Técnico", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text("Si tienes problemas con tu cuenta, contáctanos:", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("admin@chronoschola.edu", fontWeight = FontWeight.Bold)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { mostrarDialogoAyuda = false }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}

@Composable
fun PreguntaRespuesta(pregunta: String, respuesta: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(pregunta, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(respuesta, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun PantallaConfiguracion(navController: NavController) {
    val back = painterResource(R.drawable.fondo_1)
    Box {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaConfiguracion(
            navController = navController
        )
    }
}