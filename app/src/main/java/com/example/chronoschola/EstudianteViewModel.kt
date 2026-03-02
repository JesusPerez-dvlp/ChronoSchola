package com.example.chronoschola

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.chronoschola.model.Materia
import com.example.chronoschola.model.Aviso
import com.example.chronoschola.model.DatosUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class EstudianteViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _listaMaterias = mutableStateListOf<Materia>()
    val listaMaterias: List<Materia> get() = _listaMaterias
    private val _avisosPorClase = mutableStateMapOf<String, List<Aviso>>()
    private var listenerRegistro: ListenerRegistration? = null
    private var listenerAvisos: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                escucharMisClases()
            } else {
                _listaMaterias.clear()
                listenerRegistro?.remove()
                listenerAvisos?.remove()
            }
        }
    }

    fun agregarMateria(codigoIngresado: String) {
        val uidAlumno = auth.currentUser?.uid ?: return
        db.collection("clases").whereEqualTo("codigo", codigoIngresado).get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    docs.documents[0].reference.update("alumnos", FieldValue.arrayUnion(uidAlumno))
                }
            }
    }

    private fun escucharMisClases() {
        val uidAlumno = auth.currentUser?.uid ?: return
        listenerRegistro?.remove()
        listenerRegistro = db.collection("clases")
            .whereArrayContains("alumnos", uidAlumno)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    _listaMaterias.clear()
                    for (doc in snapshots) {
                        val data = doc.data
                        val idDoc = doc.id
                        val nombre = data["nombre"] as? String ?: ""
                        val horario = data["horario"] as? String ?: ""
                        val codigo = data["codigo"] as? String ?: ""
                        val fondoIndex = (data["fondoIndex"] as? Long)?.toInt() ?: 0
                        _listaMaterias.add(Materia(idDoc, nombre, horario, Color.Gray, codigo, fondoIndex))
                    }
                }
            }
    }

    fun escucharAvisosDeMateria(nombreMateria: String) {
        val materia = _listaMaterias.find { it.nombre == nombreMateria } ?: return
        val claseId = materia.id
        listenerAvisos?.remove()
        listenerAvisos = db.collection("clases").document(claseId)
            .collection("avisos")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    val lista = snapshots.map { doc ->
                        val data = doc.data
                        Aviso(
                            titulo = data["titulo"] as? String ?: "",
                            contenido = data["contenido"] as? String ?: "",
                            fecha = data["fecha"] as? String ?: ""
                        )
                    }
                    _avisosPorClase[nombreMateria] = lista
                }
            }
    }

    fun obtenerAvisos(nombreMateria: String): List<Aviso> {
        return _avisosPorClase[nombreMateria] ?: emptyList()
    }

    private val _perfilUsuario = androidx.compose.runtime.mutableStateOf(DatosUsuario())

    val perfilUsuario: androidx.compose.runtime.State<DatosUsuario> = _perfilUsuario

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
            .addOnFailureListener {
                println("Error al cargar perfil: ${it.message}")
            }
    }

    fun salirDeClase(nombreMateria: String, alTerminar: () -> Unit) {
        val uidAlumno = auth.currentUser?.uid ?: return
        val materia = _listaMaterias.find { it.nombre == nombreMateria } ?: return
        db.collection("clases").document(materia.id)
            .update("alumnos", FieldValue.arrayRemove(uidAlumno))
            .addOnSuccessListener {
                println("Saliste de la clase")
                alTerminar()
            }
            .addOnFailureListener { e ->
                println("Error al salir: ${e.message}")
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
                println("Error al actualizar perfil estudiante: ${e.message}")
            }
    }
}