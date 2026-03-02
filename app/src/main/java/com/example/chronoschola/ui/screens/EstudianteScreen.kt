package com.example.chronoschola.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chronoschola.EstudianteViewModel
import com.example.chronoschola.R
import kotlinx.coroutines.launch
import com.example.chronoschola.ui.navigation.AppRoutes
import com.example.chronoschola.ui.components.MateriaCard
import com.example.chronoschola.ui.obtenerFechaActual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoPantallaPrincipalEstudiante(navController: NavController, viewModel: EstudianteViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listaMaterias = viewModel.listaMaterias
    val fechaHoy = remember { obtenerFechaActual() }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nuevoCodigo by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                modifier = Modifier.width(260.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menú Alumno", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                NavigationDrawerItem(
                    label = { Text(text = "Mi Perfil") },
                    selected = false,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = {
                        navController.navigate(AppRoutes.PANTALLA_PERFIL_ESTUDIANTE)
                    }

                )
                NavigationDrawerItem(
                    label = { Text(text = "Configuración") },
                    selected = false,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = {
                        navController.navigate(AppRoutes.PANTALLA_CONFIGURACION)
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    navController.navigate(AppRoutes.PANTALLA_INICIO_SESION) {
                        popUpTo(AppRoutes.PANTALLA_PRINCIPAL_ESTUDIANTE) { inclusive = true }
                    }
                }, modifier = Modifier.padding(16.dp)) {
                    Text("Cerrar Sesión")
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mis Clases") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() } } ) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarDialogo = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Materia")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(text = fechaHoy, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listaMaterias) { materia ->
                        MateriaCard(
                            materia = materia,
                            onMateriaClick = { nombreMateria ->
                                // Nota: Aquí "hardcodeamos" la ruta base, pero usamos la variable nombreMateria
                                navController.navigate("detalle_materia/$nombreMateria")
                            }
                        )
                    }
                }
            }
        }
        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Inscribir Materia") },
                text = {
                    Column {
                        Text("Ingresa el código proporcionado por tu profesor:")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = nuevoCodigo,
                            onValueChange = { nuevoCodigo = it },
                            label = { Text("Código de clase (Ej: MAT-1234)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nuevoCodigo.isNotEmpty()) {
                                viewModel.agregarMateria(nuevoCodigo)
                                nuevoCodigo = ""
                                mostrarDialogo = false
                            }
                        }
                    ) {
                        Text("Inscribirse")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun PantallaPrincipalEstudiante(navController: NavController, viewModel: EstudianteViewModel) {
    val back = painterResource(R.drawable.fondo_1)
    Box {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaPrincipalEstudiante(
            navController = navController,
            viewModel = viewModel
        )
    }
}