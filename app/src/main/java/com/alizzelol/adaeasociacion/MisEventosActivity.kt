package com.alizzelol.adaeasociacion

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

class MisEventosActivity : AppCompatActivity(), EventoAdapterMisEventos.OnEventoClickListener {
    private lateinit var misEventosRecyclerView: RecyclerView
    private lateinit var eventoAdapter: EventoAdapterMisEventos
    private val misEventos: MutableList<Evento> = mutableListOf()
    private lateinit var db: FirebaseFirestore
    private lateinit var padreId: String
    private lateinit var noEventosTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_eventos)

        misEventosRecyclerView = findViewById(R.id.misEventosRecyclerView)
        misEventosRecyclerView.layoutManager = LinearLayoutManager(this)
        noEventosTextView = findViewById(R.id.noEventosTextView)

        db = FirebaseFirestore.getInstance()
        padreId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        loadMisEventos()
    }

    private fun loadMisEventos() {
        misEventos.clear()

        db.collection("inscripciones")
            .whereEqualTo("padreId", padreId)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val eventoIds: MutableList<String> = mutableListOf()

                    for (document in task.result) {
                        val inscripcion = document.toObject(Inscripcion::class.java)
                        inscripcion?.eventoId?.let { eventoIds.add(it) }
                    }

                    loadEventosDetails(eventoIds)
                } else {
                    Toast.makeText(this, "Error al cargar mis eventos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadEventosDetails(eventoIds: List<String>) {
        if (eventoIds.isEmpty()) {
            noEventosTextView.visibility = View.VISIBLE
            misEventosRecyclerView.visibility = View.GONE
            eventoAdapter = EventoAdapterMisEventos(misEventos, this@MisEventosActivity)
            misEventosRecyclerView.adapter = eventoAdapter
            return
        }

        val loadedCount = AtomicInteger(0)

        for (eventoId in eventoIds) {
            db.collection("eventos").document(eventoId).get()
                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val evento = Evento(
                            documentSnapshot.id,
                            documentSnapshot.getString("título") ?: "",
                            documentSnapshot.getString("descripción") ?: "",
                            documentSnapshot.getDate("fecha"),
                            documentSnapshot.getString("hora") ?: "",
                            documentSnapshot.getString("tipo") ?: ""
                        )
                        misEventos.add(evento)
                    }
                    val count = loadedCount.incrementAndGet()
                    if (count == eventoIds.size) {
                        Collections.sort(misEventos) { evento1, evento2 ->
                            evento1.fecha?.compareTo(evento2.fecha) ?: 0
                        }
                        eventoAdapter = EventoAdapterMisEventos(misEventos, this@MisEventosActivity)
                        misEventosRecyclerView.adapter = eventoAdapter
                        noEventosTextView.visibility = View.GONE
                        misEventosRecyclerView.visibility = View.VISIBLE
                    }
                }
        }
    }

    override fun onEventoClick(evento: Evento) {
        mostrarDialogoBorrar(evento)
    }

    private fun mostrarDialogoBorrar(evento: Evento) {
        AlertDialog.Builder(this)
            .setMessage("¿Seguro que quieres borrar ${evento.titulo} de tus eventos?")
            .setPositiveButton("Borrar") { _, _ -> borrarEvento(evento) }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun borrarEvento(evento: Evento) {
        db.collection("inscripciones")
            .whereEqualTo("padreId", padreId)
            .whereEqualTo("eventoId", evento.id)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        document.reference.delete().addOnSuccessListener {
                            Toast.makeText(this, "Evento borrado.", Toast.LENGTH_SHORT).show()
                            actualizarListaEventos(evento)
                        }.addOnFailureListener { e: Exception ->
                            Log.e("MisEventosActivity", "Error al borrar evento: ${e.message}")
                            Toast.makeText(this, "Error al borrar el evento.", Toast.LENGTH_SHORT).show()
                        }
                        return@addOnCompleteListener
                    }
                    Toast.makeText(this, "No se encontró el evento.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("MisEventosActivity", "Error al buscar inscripciones: ${task.exception?.message}")
                    Toast.makeText(this, "Error al borrar el evento.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun actualizarListaEventos(eventoBorrado: Evento) {
        misEventos.removeIf { it.id == eventoBorrado.id }
        eventoAdapter.notifyDataSetChanged()
    }
}