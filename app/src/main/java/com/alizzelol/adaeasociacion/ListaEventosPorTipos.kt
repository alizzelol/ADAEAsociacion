package com.alizzelol.adaeasociacion

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Collections

class ListaEventosPorTipos : AppCompatActivity() {
    private lateinit var recyclerViewEventos: RecyclerView
    private lateinit var eventoAdapter: EventoAdapterTipos
    private lateinit var tituloLista: TextView
    private lateinit var db: FirebaseFirestore
    private var tipoEvento: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_eventos_por_tipos)

        recyclerViewEventos = findViewById(R.id.recyclerViewEventos)
        tituloLista = findViewById(R.id.tituloLista)

        recyclerViewEventos.layoutManager = LinearLayoutManager(this)

        val intent = intent
        tipoEvento = intent.getStringExtra("tipoEvento")

        db = FirebaseFirestore.getInstance()

        tipoEvento?.let {
            tituloLista.text = "Lista de " + if (it.equals("curso", ignoreCase = true)) "Cursos" else "Talleres"
            cargarEventos(it)
        }
    }

    private fun cargarEventos(tipo: String) {
        db.collection("eventos")
            .whereEqualTo("tipo", tipo)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    val eventos = ArrayList<Evento>()
                    for (document in task.result) {
                        val evento = Evento(
                            document.id,
                            document.getString("título") ?: "",
                            document.getString("descripción") ?: "",
                            document.getDate("fecha"),
                            document.getString("hora") ?: "",
                            document.getString("tipo") ?: ""
                        )
                        eventos.add(evento)
                        Log.d(
                            "ListaEventosPorTipos",
                            "Evento cargado: ${evento.titulo}, Descripcion: ${evento.descripcion}"
                        )
                    }

                    // Ordenar eventos por fecha
                    Collections.sort(eventos) { evento1, evento2 ->
                        evento1.fecha?.compareTo(evento2.fecha) ?: 0 // Manejo de nulabilidad
                    }

                    eventoAdapter = EventoAdapterTipos(eventos)
                    recyclerViewEventos.adapter = eventoAdapter
                } else {
                    Toast.makeText(
                        this@ListaEventosPorTipos,
                        "Error al cargar eventos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}