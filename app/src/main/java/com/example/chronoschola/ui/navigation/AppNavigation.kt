package com.example.chronoschola.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chronoschola.ui.screens.*
import com.example.chronoschola.EstudianteViewModel
import com.example.chronoschola.MaestroViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val maestroViewModel: MaestroViewModel = viewModel()
    val estudianteViewModel: EstudianteViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.PANTALLA_INICIO_SESION // O PANTALLA_CARGA si prefieres
    ) {
        // --- PANTALLAS DE ACCESO ---
        composable(route = AppRoutes.PANTALLA_CARGA) {
            PantallaCarga(navController = navController)
        }
        composable(route = AppRoutes.PANTALLA_INICIO_SESION) {
            PantallaInicioSesion(navController = navController)
        }
        composable(route = AppRoutes.PANTALLA_REGISTRO) {
            PantallaRegistro(navController = navController)
        }
        composable(route = AppRoutes.PANTALLA_CONTRASENA_OLVIDADA) {
            PantallaContrasenaOlvidada(navController = navController)
        }
        composable(route = AppRoutes.PANTALLA_CONFIGURACION) {
            PantallaConfiguracion(navController = navController)
        }

        // --- RUTAS MAESTRO ---
        composable(route = AppRoutes.PANTALLA_PRINCIPAL_PROFESOR) {
            PantallaPrincipalProfesor(navController = navController, viewModel = maestroViewModel)
        }
        composable(route = AppRoutes.PANTALLA_PERFIL_MAESTRO) {
            PantallaPerfilMaestro(navController = navController, viewModel = maestroViewModel)
        }
        composable(
            route = AppRoutes.PANTALLA_DETALLE_MATERIA_MAESTRO,
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { entry ->
            val nombre = entry.arguments?.getString("nombre") ?: ""
            DetalleMateriaMaestro(navController, nombre, viewModel = maestroViewModel)
        }

        // --- RUTAS ESTUDIANTE ---
        composable(route = AppRoutes.PANTALLA_PRINCIPAL_ESTUDIANTE) {
            PantallaPrincipalEstudiante(navController = navController, viewModel = estudianteViewModel)
        }
        composable(route = AppRoutes.PANTALLA_PERFIL_ESTUDIANTE) {
            PantallaPerfilEstudiante(navController = navController, viewModel = estudianteViewModel)
        }
        composable(
            route = AppRoutes.PANTALLA_DETALLE_MATERIA,
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombreRecibido = backStackEntry.arguments?.getString("nombre")
            PantallaDetalleMateria(
                navController = navController,
                nombreMateria = nombreRecibido,
                viewModel = estudianteViewModel
            )
        }
    }
}