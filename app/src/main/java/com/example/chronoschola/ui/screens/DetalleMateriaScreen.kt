package com.example.chronoschola.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chronoschola.EstudianteViewModel
import com.example.chronoschola.MaestroViewModel
import com.example.chronoschola.model.Aviso
import com.example.chronoschola.ui.components.ItemAviso
import com.example.chronoschola.ui.components.DialogoEditarGenerico
import com.example.chronoschola.ui.listaFondosCard
import com.example.chronoschola.ui.listaColoresAvisos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleMateria(navController: NavController, nombreMateria: String?, viewModel: EstudianteViewModel) {
    if (nombreMateria == null) return
    LaunchedEffect(nombreMateria) {
        viewModel.escucharAvisosDeMateria(nombreMateria)
    }
    val listaAvisos = viewModel.obtenerAvisos(nombreMateria)
    val materiaActual = viewModel.listaMaterias.find { it.nombre == nombreMateria }
    val fondoIndex = materiaActual?.fondoIndex ?: 0
    val imagenFondo = listaFondosCard[fondoIndex % listaFondosCard.size]
    var mostrarMenu by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = imagenFondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)))
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(nombreMateria, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    actions = {
                        Box {
                            IconButton(onClick = { mostrarMenu = true }) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = mostrarMenu,
                                onDismissRequest = { mostrarMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Salir de la clase") },
                                    onClick = {
                                        mostrarMenu = false
                                        mostrarConfirmacion = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                                    }
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Avisos del Profesor",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (listaAvisos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay avisos publicados", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(listaAvisos) { index, aviso ->
                            val colorToca = listaColoresAvisos[index % listaColoresAvisos.size]
                            ItemAviso(aviso = aviso, colorFondo = colorToca)
                        }
                    }
                }
            }
        }
        if (mostrarConfirmacion) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacion = false },
                title = { Text("¿Salir de esta clase?") },
                text = { Text("Dejarás de recibir avisos y tendrás que inscribirte de nuevo con el código.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.salirDeClase(nombreMateria) {
                                navController.popBackStack()
                            }
                            mostrarConfirmacion = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Salir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleMateriaMaestro(navController: NavController, nombreMateria: String, viewModel: MaestroViewModel) {
    LaunchedEffect(nombreMateria) {
        viewModel.escucharAvisosDeMateria(nombreMateria)
    }
    val listaAvisos = viewModel.obtenerAvisos(nombreMateria)
    val codigoClase = remember { viewModel.obtenerCodigoClase(nombreMateria) }
    val materiaActual = viewModel.clasesImpartidas.find { it.nombre == nombreMateria }
    val fondoIndex = materiaActual?.fondoIndex ?: 0
    val imagenFondo = listaFondosCard[fondoIndex % listaFondosCard.size]
    var mostrarDialogoAviso by remember { mutableStateOf(false) }
    var tituloAviso by remember { mutableStateOf("") }
    var contenidoAviso by remember { mutableStateOf("") }
    var mostrarMenuAjustes by remember { mutableStateOf(false) }
    var mostrarConfirmarBorrar by remember { mutableStateOf(false) }
    var avisoABorrar by remember { mutableStateOf<Aviso?>(null) }
    var avisoAEditar by remember { mutableStateOf<Aviso?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = imagenFondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = nombreMateria, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(text = "Código: $codigoClase", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        Box {
                            IconButton(onClick = { mostrarMenuAjustes = true } ) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                            }
                        }
                        DropdownMenu(
                            expanded = mostrarMenuAjustes,
                            onDismissRequest = { mostrarMenuAjustes = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Eliminar Clase") },
                                onClick = {
                                    mostrarMenuAjustes = false
                                    mostrarConfirmarBorrar = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarDialogoAviso = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Crear Aviso")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Tablón de Anuncios",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (listaAvisos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                            Text("No hay avisos publicados", color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(listaAvisos) { index, aviso ->
                            val colorToca = listaColoresAvisos[index % listaColoresAvisos.size]
                            ItemAviso(
                                aviso = aviso,
                                colorFondo = colorToca,
                                onBorrarClick = { avisoABorrar = aviso },
                                onEditarClick = { avisoAEditar = aviso }
                            )
                        }
                    }
                }
            }
        }
    }
    if (mostrarDialogoAviso) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAviso = false },
            title = { Text("Nuevo Aviso") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tituloAviso,
                        onValueChange = { tituloAviso = it },
                        label = { Text("Título del anuncio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contenidoAviso,
                        onValueChange = { contenidoAviso = it },
                        label = { Text("Detalles") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tituloAviso.isNotEmpty() && contenidoAviso.isNotEmpty()) {
                            viewModel.publicarAviso(nombreMateria, tituloAviso, contenidoAviso)
                            tituloAviso = ""
                            contenidoAviso = ""
                            mostrarDialogoAviso = false
                        }
                    }
                ) { Text("Publicar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAviso = false }) { Text("Cancelar") }
            }
        )
    }
    if (mostrarConfirmarBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarBorrar = false },
            title = { Text("¿Eliminar esta clase?") },
            text = { Text("Esta acción no se puede deshacer. Los alumnos perderán el acceso.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.borrarClase(nombreMateria) {
                            navController.popBackStack()
                        }
                        mostrarConfirmarBorrar = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarBorrar = false }) { Text("Cancelar") }
            }
        )
    }
    if (avisoABorrar != null) {
        AlertDialog(
            onDismissRequest = { avisoABorrar = null },
            title = { Text("¿Borrar aviso?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        avisoABorrar?.let { aviso ->
                            viewModel.borrarAviso(nombreMateria, aviso.id)
                        }
                        avisoABorrar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { avisoABorrar = null }) { Text("Cancelar") }
            }
        )
    }
    if (avisoAEditar != null) {
        DialogoEditarGenerico(
            tituloDialogo = "Editar Aviso",
            valorCampo1 = avisoAEditar!!.titulo,
            labelCampo1 = "Título",
            valorCampo2 = avisoAEditar!!.contenido,
            labelCampo2 = "Contenido",
            onDismiss = { avisoAEditar = null },
            onConfirm = { nuevoTitulo, nuevoContenido ->
                viewModel.editarAviso(nombreMateria, avisoAEditar!!.id, nuevoTitulo, nuevoContenido)
                avisoAEditar = null
            }
        )
    }
}