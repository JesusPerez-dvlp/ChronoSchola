package com.example.chronoschola.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chronoschola.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.chronoschola.ui.navigation.AppRoutes

@Composable
fun PantallaCarga(navController: NavController) {
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            db.collection("usuarios").document(usuarioActual.uid).get()
                .addOnSuccessListener { document ->
                    val rol = document.getString("rol") ?: ""
                    if (rol == "Estudiante") {
                        navController.navigate(AppRoutes.PANTALLA_PRINCIPAL_ESTUDIANTE) {
                            popUpTo(0)
                        }
                    } else {
                        navController.navigate(AppRoutes.PANTALLA_PRINCIPAL_PROFESOR) {
                            popUpTo(0)
                        }
                    }
                }
                .addOnFailureListener {
                    navController.navigate(AppRoutes.PANTALLA_INICIO_SESION)
                }
        } else {
            navController.navigate(AppRoutes.PANTALLA_INICIO_SESION) {
                popUpTo(0)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ContenidoPantallaInicioSesion(modifier: Modifier = Modifier, navController: NavController){
    val usuarioImg = painterResource(R.drawable.usuario)
    val notificacionError = remember { mutableStateOf(false) }
    val mensajeError = remember { mutableStateOf("") }
    val cargando = remember { mutableStateOf(false) }
    var correo by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    var ocultar by remember { mutableStateOf(true) }
    var esRecordarme by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = "Inicio de Sesión",
            fontSize = 60.sp,
            lineHeight = 60.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )
        Image(
            painter = usuarioImg,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(280.dp)
                .height(280.dp)
                .padding(8.dp)
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electronico") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            )
        )
        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it },
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            visualTransformation = if (ocultar) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                val icon = if (ocultar) Icons.Default.Visibility else Icons.Default.VisibilityOff
                Icon(
                    imageVector = icon,
                    contentDescription = if (ocultar) "Mostrar Contraseña" else "Ocultar Contraseña",
                    modifier = Modifier.clickable { ocultar = !ocultar }
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = esRecordarme,
                onCheckedChange = { esRecordarme = it },
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                "Recordarme"
            )
            TextButton(
                onClick = {
                    navController.navigate(AppRoutes.PANTALLA_CONTRASENA_OLVIDADA)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Olvidaste tu contraseña?")
            }
        }
        Row {
            Button(
                enabled = !cargando.value,
                onClick = {
                    if (correo.isNotEmpty() && contrasenia.isNotEmpty()) {
                        cargando.value = true
                        val auth = FirebaseAuth.getInstance()
                        val db = FirebaseFirestore.getInstance()
                        auth.signInWithEmailAndPassword(correo, contrasenia)
                            .addOnSuccessListener { authResult ->
                                val uid = authResult.user?.uid ?: ""
                                db.collection("usuarios").document(uid).get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            val rol = document.getString("rol") ?: ""
                                            when (rol) {
                                                "Estudiante" -> {
                                                    navController.navigate(AppRoutes.PANTALLA_PRINCIPAL_ESTUDIANTE) {
                                                        popUpTo(AppRoutes.PANTALLA_INICIO_SESION) { inclusive = true }
                                                    }
                                                }
                                                "Profesor", "Maestro" -> {
                                                    navController.navigate(AppRoutes.PANTALLA_PRINCIPAL_PROFESOR) {
                                                        popUpTo(AppRoutes.PANTALLA_INICIO_SESION) { inclusive = true }
                                                    }
                                                }
                                                else -> {
                                                    mensajeError.value = "Error: Rol de usuario no identificado"
                                                    notificacionError.value = true
                                                    cargando.value = false
                                                }
                                            }
                                        } else {
                                            mensajeError.value = "Error: No se encontraron datos del usuario"
                                            notificacionError.value = true
                                            cargando.value = false
                                        }
                                    }
                                    .addOnFailureListener {
                                        mensajeError.value = "Error al leer base de datos"
                                        notificacionError.value = true
                                        cargando.value = false
                                    }
                            }
                            .addOnFailureListener { exception ->
                                mensajeError.value = "Error: ${exception.message}"
                                notificacionError.value = true
                                cargando.value = false
                            }
                    } else {
                        mensajeError.value = "Por favor llene todos los campos"
                        notificacionError.value = true
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "Inciar",
                    fontSize = 20.sp
                )
            }
            Button(
                onClick = {
                    navController.navigate(AppRoutes.PANTALLA_REGISTRO)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "Registrar",
                    fontSize = 20.sp
                )
            }
        }
        if(notificacionError.value) {
            AlertDialog(
                onDismissRequest = {
                    notificacionError.value = false
                },
                title = {
                    Text(text = "Problemas al iniciar sesion")
                },
                text = {
                    Text(text = "Por favor asegurese de llenar todos los campos")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            notificacionError.value = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@Composable
fun PantallaInicioSesion(modifier: Modifier = Modifier, navController: NavController) {
    val back = painterResource(R.drawable.fondo_1)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaInicioSesion(
            modifier = modifier.fillMaxSize().padding(8.dp),
            navController = navController
        )
    }
}

@Composable
fun ContenidoPantallaRegistro(modifier: Modifier = Modifier, navController: NavController) {
    val usuarioImg = painterResource(R.drawable.usuario)
    val notificacion1 = remember { mutableStateOf(false) }
    val notificacion2 = remember { mutableStateOf(false) }
    val notificacion3 = remember { mutableStateOf(false) }
    val roles = listOf("Estudiante", "Profesor")
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf("") }
    var apPaterno by remember { mutableStateOf("") }
    var apMaterno by remember { mutableStateOf("") }
    var matriculaAlumno by remember { mutableStateOf("") }
    var matriculaProfesor by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var rfc by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasenia by remember { mutableStateOf("") }
    var contrasenia2 by remember { mutableStateOf("") }
    var ocultar by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(roles[0]) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Text(
            text = "Registro",
            fontSize = 50.sp,
            lineHeight = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Image(
            painter = usuarioImg,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(280.dp)
                .height(280.dp)
                .padding(8.dp)
        )
        Text(
            text = "Asegurese de llenar todos los campos",
            fontSize = 16.sp,
            lineHeight = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            "Selecciona tu rol:",
            fontSize = 16.sp,
            lineHeight = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            roles.forEach { rol ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (rol == selectedRole),
                        onClick = { selectedRole = rol }
                    )
                    Text(
                        text = rol,
                        modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                    )
                }
            }
        }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ingrese su nombre") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            )
        )
        OutlinedTextField(
            value = apPaterno,
            onValueChange = { apPaterno = it },
            label = { Text("Ingrese su apellido paterno") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            )
        )
        OutlinedTextField(
            value = apMaterno,
            onValueChange = { apMaterno = it },
            label = { Text("Ingrese su apellido materno") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            )
        )
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Ingrese su correo") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            )
        )
        if(selectedRole == "Estudiante") {
            OutlinedTextField(
                value = matriculaAlumno,
                onValueChange = { matriculaAlumno = it },
                label = { Text("Ingrese su matricula") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .padding(5.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.Black,
                )
            )
            OutlinedTextField(
                value = curp,
                onValueChange = { curp = it },
                label = { Text("Ingrese su CURP") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .padding(5.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.Black,
                )
            )
        }else{
            OutlinedTextField(
                value = matriculaProfesor,
                onValueChange = { matriculaProfesor = it },
                label = { Text("Ingrese su matricula") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .padding(5.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.Black,
                )
            )
            OutlinedTextField(
                value = rfc,
                onValueChange = { rfc = it },
                label = { Text("Ingrese su RFC") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .padding(5.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.Black,
                )
            )
            OutlinedTextField(
                value = cedula,
                onValueChange = { cedula = it },
                label = { Text("Ingrese su cedula profesional") },
                singleLine = true,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .padding(5.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color.Black,
                )
            )
        }
        OutlinedTextField(
            value = contrasenia,
            onValueChange = { contrasenia = it
                isError = false},
            label = { Text("Ingrese la contraseña") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            visualTransformation = if (ocultar) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                val icon = if (ocultar) Icons.Default.Visibility else Icons.Default.VisibilityOff
                Icon(
                    imageVector = icon,
                    contentDescription = if (ocultar) "Mostrar Contraseña" else "Ocultar Contraseña",
                    modifier = Modifier.clickable { ocultar = !ocultar }
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            ),
            isError = isError
        )
        OutlinedTextField(
            value = contrasenia2,
            onValueChange = { contrasenia2 = it
                isError = false},
            label = { Text("Confirme la contraseña") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .padding(5.dp),
            visualTransformation = if (ocultar) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                val icon = if (ocultar) Icons.Default.Visibility else Icons.Default.VisibilityOff
                Icon(
                    imageVector = icon,
                    contentDescription = if (ocultar) "Mostrar Contraseña" else "Ocultar Contraseña",
                    modifier = Modifier.clickable { ocultar = !ocultar }
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            ),
            isError = isError
        )
        Row {
            Button(onClick = {
                navController.popBackStack()
            },
                modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Cancelar",
                    fontSize = 20.sp
                )
            }
            Button(
                onClick = {
                    val camposComunesLlenos = name.isNotEmpty() && apPaterno.isNotEmpty() && apMaterno.isNotEmpty() && correo.isNotEmpty() && contrasenia.isNotEmpty() && contrasenia2.isNotEmpty()
                    val campoEspecificoLleno = if (selectedRole == "Estudiante") {
                        matriculaAlumno.isNotEmpty() && curp.isNotEmpty()
                    } else {
                        matriculaProfesor.isNotEmpty() && cedula.isNotEmpty() && rfc.isNotEmpty()
                    }
                    if (!camposComunesLlenos || !campoEspecificoLleno || contrasenia != contrasenia2) {
                        isError = true
                        notificacion3.value = true
                    } else {
                        val auth = FirebaseAuth.getInstance()
                        val db = FirebaseFirestore.getInstance()
                        auth.createUserWithEmailAndPassword(correo, contrasenia)
                            .addOnSuccessListener { authResult ->
                                val uid = authResult.user?.uid ?: ""
                                val usuarioData = hashMapOf(
                                    "id" to uid,
                                    "nombre" to name,
                                    "apellidoPaterno" to apPaterno,
                                    "apellidoMaterno" to apMaterno,
                                    "correo" to correo,
                                    "rol" to selectedRole
                                )
                                if (selectedRole == "Estudiante") {
                                    usuarioData["matricula"] = matriculaAlumno
                                    usuarioData["curp"] = curp
                                } else {
                                    usuarioData["matricula"] = matriculaProfesor
                                    usuarioData["rfc"] = rfc
                                    usuarioData["cedula"] = cedula
                                }
                                db.collection("usuarios").document(uid).set(usuarioData)
                                    .addOnSuccessListener {
                                        isError = false
                                        notificacion1.value = true
                                    }
                                    .addOnFailureListener { e ->
                                        println("Error al guardar datos: ${e.message}")
                                    }
                            }
                            .addOnFailureListener { exception ->
                                isError = true
                                println("Error al crear usuario: ${exception.message}")
                                notificacion2.value = true
                            }
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "Registrar",
                    fontSize = 20.sp
                )
            }
        }
        TextButton(
            onClick = {
                notificacion2.value = true
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Tiene algun problema para registrarse?")
        }
        if(notificacion1.value) {
            AlertDialog(
                onDismissRequest = {
                    navController.popBackStack()
                },
                title = {
                    Text(text = "Registro")
                },
                text = {
                    Text(text = "Usuario registrado")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Text("Iniciar Sesion")
                    }
                }
            )
        }
        if(notificacion2.value) {
            AlertDialog(
                onDismissRequest = {
                    notificacion2.value = false
                },
                title = {
                    Text(text = "Problemas al registrarse")
                },
                text = {
                    Text(text = "Para obtener ayuda contacte con el administrador")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            notificacion2.value = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
        if(notificacion3.value){
            AlertDialog(
                onDismissRequest = {
                    notificacion3.value = false
                },
                title = {
                    Text(text = "Problemas al registrarse")
                },
                text = {
                    Text(text = "Por favor asegurese de llenar todos los campos")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            notificacion3.value = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}

@Composable
fun PantallaRegistro(modifier: Modifier = Modifier, navController: NavController) {
    val back = painterResource(R.drawable.fondo_1)
    Box {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaRegistro(
            modifier = modifier.fillMaxSize().padding(8.dp),
            navController = navController
        )
    }
}

@Composable
fun ContenidoPantallaContrasenaOlvidada(modifier: Modifier = Modifier, navController: NavController) {
    val correoImg = painterResource(R.drawable.coreo)
    val notificacionExito = remember { mutableStateOf(false) }
    val notificacionError = remember { mutableStateOf(false) }
    val mensajeError = remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        Text(
            text = "Recuperar contraseña",
            fontSize = 40.sp,
            lineHeight = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )
        Image(
            painter = correoImg,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .padding(8.dp)
        )
        Text(
            text = "Ingrese el correo vinculado a su cuenta para enviarle un enlace de recuperación.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                isError = false
            },
            label = { Text("Ingrese el correo") },
            singleLine = true,
            modifier = Modifier
                .width(300.dp)
                .padding(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black,
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color.Black,
            ),
            isError = isError
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(8.dp),
                enabled = !cargando
            ) {
                Text("Cancelar", fontSize = 18.sp)
            }
            Button(
                enabled = !cargando,
                onClick = {
                    if (correo.isNotEmpty()) {
                        cargando = true
                        val auth = FirebaseAuth.getInstance()
                        auth.sendPasswordResetEmail(correo)
                            .addOnCompleteListener { task ->
                                cargando = false
                                if (task.isSuccessful) {
                                    notificacionExito.value = true
                                } else {
                                    isError = true
                                    mensajeError.value = task.exception?.message ?: "Error desconocido"
                                    notificacionError.value = true
                                }
                            }
                    } else {
                        isError = true
                        mensajeError.value = "El campo de correo no puede estar vacío"
                        notificacionError.value = true
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(if (cargando) "Enviando..." else "Recuperar", fontSize = 18.sp)
            }
        }
        if(notificacionExito.value) {
            AlertDialog(
                onDismissRequest = {
                    notificacionExito.value = false
                    navController.popBackStack()
                },
                title = { Text(text = "Correo Enviado") },
                text = { Text(text = "Revise su bandeja de entrada (y Spam). Hemos enviado un enlace para restablecer su contraseña.") },
                confirmButton = {
                    Button(
                        onClick = {
                            notificacionExito.value = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Ir al Login")
                    }
                }
            )
        }
        if(notificacionError.value) {
            AlertDialog(
                onDismissRequest = { notificacionError.value = false },
                title = { Text(text = "Error") },
                text = { Text(text = mensajeError.value) },
                confirmButton = {
                    Button(onClick = { notificacionError.value = false }) {
                        Text("Intentar de nuevo")
                    }
                }
            )
        }
    }
}

@Composable
fun PantallaContrasenaOlvidada(modifier: Modifier = Modifier, navController: NavController) {
    val back = painterResource(R.drawable.fondo_1)
    Box {
        Image(
            painter = back,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )
        ContenidoPantallaContrasenaOlvidada(
            modifier = modifier.fillMaxSize().padding(8.dp),
            navController = navController
        )
    }
}