package com.alizzelol.adaeasociacion

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*

interface OnListaEventoClickListener {
    fun onListaEventoClick(eventoId: String)
}

class ListaEventosActivity : AppCompatActivity() {
    private var recyclerViewEventos: RecyclerView? = null
    private var listaEventoAdapter: ListaEventosAdapter? = null
    private val listaEventos: MutableList<Evento> = ArrayList()
    private var db: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_eventos)

        recyclerViewEventos = findViewById(R.id.recyclerViewEventos)
        recyclerViewEventos?.layoutManager = LinearLayoutManager(this)

        db = FirebaseFirestore.getInstance()
        cargarEventos()
    }

    private fun cargarEventos() {
        db?.collection("eventos")
            ?.get()
            ?.addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    listaEventos.clear()
                    for (document in task.result) {
                        val evento = Evento(
                            document.id, // Directamente usa document.id, ya que es String no nullable
                            document.getString("título") ?: "",
                            document.getString("descripción") ?: "",
                            document.getDate("fecha"),
                            document.getString("hora") ?: "",
                            document.getString("tipo") ?: ""
                        )
                        listaEventos.add(evento)
                    }
                    listaEventoAdapter = ListaEventosAdapter(listaEventos,
                        object : OnListaEventoClickListener {
                            override fun onListaEventoClick(eventoId: String) {
                                mostrarDetallesEvento(eventoId)
                            }
                        })
                    recyclerViewEventos?.adapter = listaEventoAdapter
                } else {
                    Toast.makeText(this, "Error al cargar eventos.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun mostrarDetallesEvento(eventoId: String) {
        db?.collection("eventos")?.document(eventoId)
            ?.get()
            ?.addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                if (documentSnapshot.exists()) {
                    val titulo = documentSnapshot.getString("título")
                    val descripcion = documentSnapshot.getString("descripción")
                    val fechaDate = documentSnapshot.getDate("fecha")
                    val hora = documentSnapshot.getString("hora")
                    val tipo = documentSnapshot.getString("tipo")

                    val sdf =
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val fecha = sdf.format(fechaDate)

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(titulo)
                        .setMessage(
                            """
                                Título: $titulo
                                Descripción: $descripcion
                                Fecha: $fecha
                                Hora: $hora
                                Tipo: $tipo
                            """.trimIndent()
                        )
                        .setPositiveButton(
                            "Cerrar"
                        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                        .setNegativeButton(
                            "Eliminar"
                        ) { _: DialogInterface?, _: Int -> eliminarEvento(eventoId) }
                        .setNeutralButton(
                            "Padres"
                        ) { _: DialogInterface?, _: Int ->
                            mostrarPadresInscritos(eventoId)
                        }
                        .show()
                } else {
                    Toast.makeText(this, "Evento no encontrado.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            ?.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al cargar detalles del evento.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun mostrarPadresInscritos(eventoId: String) {
        val intent = Intent(this, ListaPadresInscritos::class.java)
        intent.putExtra("eventoId", eventoId)
        startActivity(intent)
    }

    private fun eliminarEvento(eventoId: String) {
        db?.collection("eventos")?.document(eventoId)
            ?.delete()
            ?.addOnSuccessListener {
                Toast.makeText(this, "Evento eliminado con éxito.", Toast.LENGTH_SHORT)
                    .show()
                cargarEventos() // Recargar la lista de eventos
            }
            ?.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Error al eliminar evento.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}