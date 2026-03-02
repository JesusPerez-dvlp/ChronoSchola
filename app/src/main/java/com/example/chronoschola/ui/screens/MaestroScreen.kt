package com.example.chronoschola.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chronoschola.MaestroViewModel
import com.example.chronoschola.R
import kotlinx.coroutines.launch
import com.example.chronoschola.model.Materia
import com.example.chronoschola.ui.navigation.AppRoutes
import com.example.chronoschola.ui.components.MateriaCard
import com.example.chronoschola.ui.components.DialogoEditarGenerico
import com.example.chronoschola.ui.obtenerFechaActual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPantallaPrincipalProfesor(navController: NavController, viewModel: MaestroViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listaClases = viewModel.clasesImpartidas
    val fechaHoy = remember { obtenerFechaActual() }
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoHorario by remember { mutableStateOf("") }
    var mostrarDialogoEditarClase by remember { mutableStateOf(false) }
    var claseAEditar by remember { mutableStateOf<Materia?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                modifier = Modifier.width(260.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menú Docente", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    selected = false,
                    onClick = {
                        navController.navigate(AppRoutes.PANTALLA_PERFIL_MAESTRO)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Configuración") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    selected = false,
                    onClick = {
                        navController.navigate(AppRoutes.PANTALLA_CONFIGURACION)
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        navController.navigate(AppRoutes.PANTALLA_INICIO_SESION) {
                            popUpTo(AppRoutes.PANTALLA_PRINCIPAL_PROFESOR) { inclusive = true }
                        }
                    }, modifier = Modifier.padding(16.dp)
                ) { Text("Cerrar Sesión") }
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Panel del Maestro") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { mostrarDialogoCrear = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crear Clase")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text(
                    text = fechaHoy,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                if (listaClases.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No has creado ninguna clase aún", color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(listaClases) { materia ->
                            MateriaCard(
                                materia = materia,
                                onMateriaClick = { nombre ->
                                    navController.navigate("detalle_materia_maestro/$nombre")
                                },
                                onEditarClick = {
                                    claseAEditar = materia
                                    mostrarDialogoEditarClase = true
                                }
                            )
                        }
                    }
                }
            }
        }
        if (mostrarDialogoCrear) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCrear = false },
                title = { Text("Crear Nueva Clase") },
                text = {
                    Column {
                        Text("Define los detalles de tu curso.")
                        Text("El código de acceso se generará automáticamente.", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = nuevoNombre,
                            onValueChange = { nuevoNombre = it },
                            label = { Text("Nombre de la Asignatura") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nuevoHorario,
                            onValueChange = { nuevoHorario = it },
                            label = { Text("Horario") },
                            placeholder = { Text("Ej: Lun/Mie 10:00") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevoNombre.isNotEmpty() && nuevoHorario.isNotEmpty()) {
                                viewModel.crearClase(nuevoNombre, nuevoHorario)
                                nuevoNombre = ""
                                nuevoHorario = ""
                                mostrarDialogoCrear = false
                            }
                        }
                    ) { Text("Crear") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoCrear = false }) { Text("Cancelar") }
                }
            )
        }
        if (mostrarDialogoEditarClase && claseAEditar != null) {
            DialogoEditarGenerico(
                tituloDialogo = "Editar Clase",
                valorCampo1 = claseAEditar!!.nombre,
                labelCampo1 = "Nombre",
                valorCampo2 = claseAEditar!!.horario,
                labelCampo2 = "Horario",
                onDismiss = { mostrarDialogoEditarClase = false },
                onConfirm = { nuevoNom, nuevoHor ->
                    viewModel.editarClase(claseAEditar!!.id, nuevoNom, nuevoHor)
                    mostrarDialogoEditarClase = false
                }
            )
        }
    }
}

@Composable
fun PantallaPrincipalProfesor(navController: NavController,viewModel: MaestroViewModel){
    val back = painterResource(R.drawable.fondo_1)
    Box {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaPrincipalProfesor(
            navController = navController,
            viewModel = viewModel
        )
    }
}