package com.example.chronoschola.model

data class DatosUsuario(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val correo: String = "",
    val matricula: String = "",
    val rol: String = "",
    val cedula: String = "",
    val curp: String = "",
    val rfc: String = "",
    val avatarIndex: Int = 0
)