package com.example.chronoschola.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chronoschola.EstudianteViewModel
import com.example.chronoschola.MaestroViewModel
import com.example.chronoschola.R
import com.example.chronoschola.ui.listaAvatares

@Composable
fun ContenidoPantallaPerfilMaestro(navController: NavController, viewModel: MaestroViewModel) {
    LaunchedEffect(Unit) { viewModel.cargarPerfil() }
    val usuario = viewModel.perfilUsuario.value
    var editando by remember { mutableStateOf(false) }
    var mostrarDialogoAvatar by remember { mutableStateOf(false) }
    var tempNombre by remember { mutableStateOf("") }
    var tempPaterno by remember { mutableStateOf("") }
    var tempMaterno by remember { mutableStateOf("") }
    var tempAvatarIndex by remember { mutableStateOf(0) }
    LaunchedEffect(editando) {
        if (editando) {
            tempNombre = usuario.nombre
            tempPaterno = usuario.apellidoPaterno
            tempMaterno = usuario.apellidoMaterno
            tempAvatarIndex = usuario.avatarIndex
        }
    }
    val indexMostrar = if (editando) tempAvatarIndex else usuario.avatarIndex
    val imagenAvatar = listaAvatares[indexMostrar % listaAvatares.size]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Text("Perfil Docente", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(imagenAvatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(4.dp, if (editando) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                    .clickable(enabled = editando) {
                        mostrarDialogoAvatar = true
                    }
            )
            if (editando) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (editando) {
            OutlinedTextField(
                value = tempNombre,
                onValueChange = { tempNombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tempPaterno,
                onValueChange = { tempPaterno = it },
                label = { Text("Apellido Paterno") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tempMaterno,
                onValueChange = { tempMaterno = it },
                label = { Text("Apellido Materno") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = "${usuario.nombre} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = usuario.correo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Datos Institucionales", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                DatoPerfil("Matrícula", usuario.matricula)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DatoPerfil("Cédula", usuario.cedula)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DatoPerfil("RFC", usuario.rfc)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (editando) {
                    viewModel.actualizarPerfilCompleto(tempNombre, tempPaterno, tempMaterno, tempAvatarIndex)
                    editando = false
                } else {
                    editando = true
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(if (editando) "Guardar Cambios" else "Editar Perfil")
        }
    }
    if (mostrarDialogoAvatar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAvatar = false },
            title = { Text("Elige tu Avatar") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(listaAvatares) { index, imgRes ->
                        Image(
                            painter = painterResource(imgRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (tempAvatarIndex == index) 4.dp else 0.dp,
                                    color = if (tempAvatarIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    tempAvatarIndex = index
                                    mostrarDialogoAvatar = false
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoAvatar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PantallaPerfilMaestro(navController: NavController, viewModel: MaestroViewModel) {
    val back = painterResource(R.drawable.fondo_1)
    Box {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaPerfilMaestro(
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun ContenidoPantallaPerfilEstudiante(navController: NavController, viewModel: EstudianteViewModel) {
    LaunchedEffect(Unit) { viewModel.cargarPerfil() }
    val usuario = viewModel.perfilUsuario.value
    var editando by remember { mutableStateOf(false) }
    var mostrarDialogoAvatar by remember { mutableStateOf(false) }
    var tempNombre by remember { mutableStateOf("") }
    var tempPaterno by remember { mutableStateOf("") }
    var tempMaterno by remember { mutableStateOf("") }
    var tempAvatarIndex by remember { mutableStateOf(0) }
    LaunchedEffect(editando) {
        if (editando) {
            tempNombre = usuario.nombre
            tempPaterno = usuario.apellidoPaterno
            tempMaterno = usuario.apellidoMaterno
            tempAvatarIndex = usuario.avatarIndex
        }
    }
    val indexMostrar = if (editando) tempAvatarIndex else usuario.avatarIndex
    val imagenAvatar = listaAvatares[indexMostrar % listaAvatares.size]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
            }
            Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(imagenAvatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(4.dp, if (editando) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary, CircleShape)
                    .clickable(enabled = editando) { mostrarDialogoAvatar = true }
            )
            if (editando) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (editando) {
            OutlinedTextField(value = tempNombre, onValueChange = { tempNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = tempPaterno, onValueChange = { tempPaterno = it }, label = { Text("Apellido Paterno") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = tempMaterno, onValueChange = { tempMaterno = it }, label = { Text("Apellido Materno") }, modifier = Modifier.fillMaxWidth())
        } else {
            Text(
                text = "${usuario.nombre} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Estudiante - ${usuario.matricula}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Información Académica", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                DatoPerfil(label = "Correo", valor = usuario.correo)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DatoPerfil(label = "CURP", valor = usuario.curp)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DatoPerfil(label = "Matrícula", valor = usuario.matricula)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (editando) {
                    viewModel.actualizarPerfilCompleto(tempNombre, tempPaterno, tempMaterno, tempAvatarIndex)
                    editando = false
                } else {
                    editando = true
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(if (editando) "Guardar Cambios" else "Editar Perfil")
        }
    }
    if (mostrarDialogoAvatar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAvatar = false },
            title = { Text("Elige tu Avatar") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(listaAvatares) { index, imgRes ->
                        Image(
                            painter = painterResource(imgRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (tempAvatarIndex == index) 4.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable {
                                    tempAvatarIndex = index
                                    mostrarDialogoAvatar = false
                                }
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { mostrarDialogoAvatar = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun DatoPerfil(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PantallaPerfilEstudiante(navController: NavController, viewModel: EstudianteViewModel) {
    val back = painterResource(R.drawable.fondo_1)
    Box {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaPerfilEstudiante(
            navController = navController,
            viewModel = viewModel
        )
    }
}
