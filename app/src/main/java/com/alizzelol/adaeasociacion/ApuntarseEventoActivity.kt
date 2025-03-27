package com.alizzelol.adaeasociacion

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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

class ApuntarseEventoActivity : AppCompatActivity(), EventoAdapterApuntarse.OnEventoClickListener {

    private var eventosRecyclerView: RecyclerView? = null
    private var eventoAdapter: EventoAdapterApuntarse? = null
    private var eventos: MutableList<Evento>? = null
    private var db: FirebaseFirestore? = null
    private var padreId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apuntarse_evento)

        eventosRecyclerView = findViewById(R.id.eventosRecyclerView)
        eventosRecyclerView?.layoutManager = LinearLayoutManager(this)

        eventos = ArrayList()
        db = FirebaseFirestore.getInstance()

        padreId = FirebaseAuth.getInstance().currentUser?.uid

        loadEvents()
    }

    private fun loadEvents() {
        db?.collection("eventos")
            ?.get()
            ?.addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    eventos?.clear()
                    for (document in task.result) {
                        val evento = Evento(
                            document.id,
                            document.getString("título")!!,
                            document.getString("descripción")!!,
                            document.getDate("fecha"),
                            document.getString("hora")!!,
                            document.getString("tipo")!!
                        )
                        eventos?.add(evento)
                    }
                    eventoAdapter = eventos?.let { EventoAdapterApuntarse(it, this) }
                    eventosRecyclerView?.adapter = eventoAdapter
                } else {
                    Toast.makeText(this, "Error al cargar eventos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onEventoClick(eventoId: String?) {
        if (eventoId != null) {
            mostrarDialogoApuntarse(eventoId)
        } else {
            Toast.makeText(this, "ID de evento nulo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoApuntarse(eventoId: String) {
        db?.collection("eventos")?.document(eventoId)?.get()
            ?.addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                if (documentSnapshot.exists()) {
                    val evento = Evento(
                        documentSnapshot.id,
                        documentSnapshot.getString("título")!!,
                        documentSnapshot.getString("descripción")!!,
                        documentSnapshot.getDate("fecha"),
                        documentSnapshot.getString("hora")!!,
                        documentSnapshot.getString("tipo")!!
                    )
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("¿Seguro que quieres apuntarte a " + evento.titulo + "?")
                        .setPositiveButton(
                            "Apuntarse"
                        ) { _, _ -> apuntarUsuarioEvento(eventoId) }
                        .setNegativeButton(
                            "Cancelar"
                        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                        .show()
                }
            }
    }

    private fun apuntarUsuarioEvento(eventoId: String) {
        val inscripcion = Inscripcion(padreId, eventoId)
        db?.collection("inscripciones")
            ?.add(inscripcion)
            ?.addOnSuccessListener {
                Toast.makeText(this, "Apuntado al evento.", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { e: Exception ->
                Log.e("ApuntarseEventoActivity", "Error al apuntarse: " + e.message)
                Toast.makeText(this, "Error al apuntarse.", Toast.LENGTH_SHORT).show()
            }
    }
}