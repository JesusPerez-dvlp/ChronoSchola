package com.example.chronoschola.ui.navigation

object AppRoutes {
    const val PANTALLA_INICIO_SESION = "pantalla_inicio_sesion"
    const val PANTALLA_REGISTRO = "pantalla_registro"
    const val PANTALLA_CONTRASENA_OLVIDADA = "pantalla_contrasena_olvidada"

    //Maestro
    const val PANTALLA_PRINCIPAL_PROFESOR = "pantalla_principal_profesor"
    const val PANTALLA_PERFIL_MAESTRO = "pantalla_perfil_maestro"
    const val PANTALLA_DETALLE_MATERIA_MAESTRO = "detalle_materia_maestro/{nombre}"

    //Estudiante
    const val PANTALLA_PRINCIPAL_ESTUDIANTE = "pantalla_principal_estudiante"
    const val PANTALLA_PERFIL_ESTUDIANTE = "pantalla_perfil_estudiante"
    const val PANTALLA_DETALLE_MATERIA = "detalle_materia/{nombre}"

    //
    const val PANTALLA_CONFIGURACION = "pantalla_configuracion"
    const val PANTALLA_CARGA ="pantalla_carga"
}