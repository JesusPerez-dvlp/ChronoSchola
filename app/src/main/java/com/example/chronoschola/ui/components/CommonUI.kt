package com.example.chronoschola.ui.components

import com.example.chronoschola.model.Aviso
import com.example.chronoschola.model.Materia
import com.example.chronoschola.ui.listaFondosCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ItemAviso(aviso: Aviso, colorFondo: Color, onBorrarClick: (() -> Unit)? = null, onEditarClick: (() -> Unit)? = null) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = aviso.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.weight(1f)
                )
                Row {
                    if (onEditarClick != null) {
                        IconButton(onClick = onEditarClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Black.copy(0.6f))
                        }
                    }
                    if (onBorrarClick != null) {
                        IconButton(onClick = onBorrarClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red.copy(0.6f))
                        }
                    }
                }
            }
            Text(text = aviso.fecha, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Divider(color = Color.Black.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
            Text(text = aviso.contenido, style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun DialogoEditarGenerico(tituloDialogo: String, valorCampo1: String, labelCampo1: String, valorCampo2: String, labelCampo2: String, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var texto1 by remember { mutableStateOf(valorCampo1) }
    var texto2 by remember { mutableStateOf(valorCampo2) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = tituloDialogo) },
        text = {
            Column {
                OutlinedTextField(
                    value = texto1,
                    onValueChange = { texto1 = it },
                    label = { Text(labelCampo1) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = texto2,
                    onValueChange = { texto2 = it },
                    label = { Text(labelCampo2) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(texto1, texto2) }) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun MateriaCard(materia: Materia, onMateriaClick: (String) -> Unit, onEditarClick: (() -> Unit)? = null) {
    val imagenReal = listaFondosCard[materia.fondoIndex % listaFondosCard.size]
    Card(
        modifier = Modifier
            .height(140.dp)
            .clickable { onMateriaClick(materia.nombre) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imagenReal),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            if (onEditarClick != null) {
                Box(modifier = Modifier.fillMaxSize().padding(4.dp), contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = onEditarClick,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape).size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = materia.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = materia.horario,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.TopStart) {
                Text(
                    text = materia.codigo,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
