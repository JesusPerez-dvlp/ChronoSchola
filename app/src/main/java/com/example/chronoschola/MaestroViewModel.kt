package com.example.chronoschola

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.chronoschola.model.Materia
import com.example.chronoschola.model.Aviso
import com.example.chronoschola.model.DatosUsuario
import com.example.chronoschola.ui.listaFondosCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlin.random.Random

class MaestroViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _clasesImpartidas = mutableStateListOf<Materia>()
    val clasesImpartidas: List<Materia> get() = _clasesImpartidas
    private val _avisosPorClase = mutableStateMapOf<String, List<Aviso>>()
    private var listenerClases: ListenerRegistration? = null
    private var listenerAvisos: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                escucharClasesEnTiempoReal()
            } else {
                _clasesImpartidas.clear()
                listenerClases?.remove()
                listenerAvisos?.remove()
            }
        }
    }

    private fun escucharClasesEnTiempoReal() {
        val uidUsuario = auth.currentUser?.uid ?: return
        listenerClases?.remove()
        listenerClases = db.collection("clases")
            .whereEqualTo("idProfesor", uidUsuario)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    _clasesImpartidas.clear()
                    for (doc in snapshots) {
                        val data = doc.data
                        val idDoc = doc.id
                        val nombre = data["nombre"] as? String ?: ""
                        val horario = data["horario"] as? String ?: ""
                        val codigo = data["codigo"] as? String ?: ""
                        val fondoIndex = (data["fondoIndex"] as? Long)?.toInt() ?: 0
                        _clasesImpartidas.add(
                            Materia(
                                idDoc,
                                nombre,
                                horario,
                                Color.Gray,
                                codigo,
                                fondoIndex
                            )
                        )
                    }
                }
            }
    }

    fun crearClase(nombre: String, horario: String) {
        val uidUsuario = auth.currentUser?.uid ?: return
        val indexAleatorio = (listaFondosCard.indices).random()
        val nuevaClaseMap = hashMapOf(
            "nombre" to nombre,
            "horario" to horario,
            "codigo" to generarCodigoClase(nombre),
            "idProfesor" to uidUsuario,
            "alumnos" to emptyList<String>(),
            "fondoIndex" to indexAleatorio
        )
        db.collection("clases").add(nuevaClaseMap)
    }

    fun escucharAvisosDeMateria(nombreMateria: String) {
        val materia = _clasesImpartidas.find { it.nombre == nombreMateria } ?: return
        val claseId = materia.id
        listenerAvisos?.remove()
        listenerAvisos = db.collection("clases").document(claseId)
            .collection("avisos")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    val listaAvisos = snapshots.map { doc ->
                        val data = doc.data
                        Aviso(
                            id = doc.id,
                            titulo = data["titulo"] as? String ?: "",
                            contenido = data["contenido"] as? String ?: "",
                            fecha = data["fecha"] as? String ?: ""
                        )
                    }
                    _avisosPorClase[nombreMateria] = listaAvisos
                }
            }
    }

    fun publicarAviso(nombreMateria: String, titulo: String, contenido: String) {
        val materia = _clasesImpartidas.find { it.nombre == nombreMateria } ?: return
        val claseId = materia.id
        val fechaActual = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        val avisoMap = hashMapOf(
            "titulo" to titulo,
            "contenido" to contenido,
            "fecha" to fechaActual
        )
        db.collection("clases").document(claseId)
            .collection("avisos")
            .add(avisoMap)
    }

    fun obtenerAvisos(nombreMateria: String): List<Aviso> {
        return _avisosPorClase[nombreMateria] ?: emptyList()
    }

    fun obtenerCodigoClase(nombreMateria: String): String {
        return _clasesImpartidas.find { it.nombre == nombreMateria }?.codigo ?: "..."
    }

    private fun generarCodigoClase(nombre: String): String {
        val prefijo = nombre.take(3).uppercase()
        val numero = Random.nextInt(1000, 9999)
        return "$prefijo-$numero"
    }

    private val _perfilUsuario = androidx.compose.runtime.mutableStateOf(DatosUsuario())

    val perfilUsuario: androidx.compose.runtime.State<DatosUsuario> = _perfilUsuario

    fun borrarClase(nombreMateria: String, alTerminar: () -> Unit) {
        val materia = _clasesImpartidas.find { it.nombre == nombreMateria } ?: return
        db.collection("clases").document(materia.id)
            .delete()
            .addOnSuccessListener {
                println("Clase eliminada")
                alTerminar()
            }
            .addOnFailureListener { e ->
                println("Error al borrar: ${e.message}")
            }
    }

    fun borrarAviso(nombreMateria: String, idAviso: String) {
        val materia = _clasesImpartidas.find { it.nombre == nombreMateria } ?: return

        db.collection("clases").document(materia.id)
            .collection("avisos").document(idAviso)
            .delete()
            .addOnSuccessListener { println("Aviso borrado") }
            .addOnFailureListener { println("Error al borrar aviso: ${it.message}") }
    }

    fun editarClase(idClase: String, nuevoNombre: String, nuevoHorario: String) {
        val updates = hashMapOf<String, Any>(
            "nombre" to nuevoNombre,
            "horario" to nuevoHorario
        )

        db.collection("clases").document(idClase)
            .update(updates)
            .addOnSuccessListener {
                println("Clase actualizada con éxito")
            }
            .addOnFailureListener { e ->
                println("Error al actualizar clase: ${e.message}")
            }
    }

    fun editarAviso(nombreMateria: String, idAviso: String, nuevoTitulo: String, nuevoContenido: String) {
        val materia = _clasesImpartidas.find { it.nombre == nombreMateria } ?: return

        val updates = hashMapOf<String, Any>(
            "titulo" to nuevoTitulo,
            "contenido" to nuevoContenido,
        )

        db.collection("clases").document(materia.id)
            .collection("avisos").document(idAviso)
            .update(updates)
            .addOnSuccessListener { println("Aviso actualizado") }
            .addOnFailureListener { e -> println("Error al actualizar aviso: ${e.message}") }
    }

    fun cargarPerfil() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data
                    val usuario = DatosUsuario(
                        nombre = data?.get("nombre") as? String ?: "",
                        apellidoPaterno = data?.get("apellidoPaterno") as? String ?: "",
                        apellidoMaterno = data?.get("apellidoMaterno") as? String ?: "",
                        correo = data?.get("correo") as? String ?: "",
                        matricula = data?.get("matricula") as? String ?: "",
                        rol = data?.get("rol") as? String ?: "",
                        cedula = data?.get("cedula") as? String ?: "",
                        curp = data?.get("curp") as? String ?: "",
                        rfc = data?.get("rfc") as? String ?: "",
                        avatarIndex = (data?.get("avatarIndex") as? Long)?.toInt() ?: 0
                    )
                    _perfilUsuario.value = usuario
                }
            }
    }

    fun actualizarPerfilCompleto(nombre: String, paterno: String, materno: String, avatarIdx: Int) {
        val uid = auth.currentUser?.uid ?: return

        val updates = hashMapOf<String, Any>(
            "nombre" to nombre,
            "apellidoPaterno" to paterno,
            "apellidoMaterno" to materno,
            "avatarIndex" to avatarIdx
        )

        db.collection("usuarios").document(uid)
            .update(updates)
            .addOnSuccessListener {
                cargarPerfil()
            }
            .addOnFailureListener { e ->
                println("Error al actualizar perfil: ${e.message}")
            }
    }
}